package repositories

import models.{User, UserWithPassword}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.implicitConversions

class MongoUsersRepositorySpec extends PlaySpec with GuiceOneAppPerTest with BeforeAndAfterEach {
  val injector: Injector = new GuiceApplicationBuilder().build.injector
  val mongo: ReactiveMongoApi = injector.instanceOf[ReactiveMongoApi]
  val repository: UsersRepository = injector.instanceOf[MongoUsersRepository]

  val MAX_DURATION: FiniteDuration = 2 seconds

  val TEST_USER_1 = UserWithPassword("admin", "password", Set("admin"))
  val TEST_USER_2 = UserWithPassword("test", "password", Set("read-a", "write-b"))

  implicit def userWithPasswordToUser(user: UserWithPassword): User = User(
    username = user.username,
    permissions = user.permissions
  )

  override protected def beforeEach(): Unit = {
    Await.ready(mongo.database.map(db => db.drop()), MAX_DURATION)
    System.out.println("Drop database in MongoDB... [done]")
  }

  "MongoUsersRepository" when {

    "#authenticate" should {
      "return empty if the username does not match any user" in {
        Await.result(repository.create(TEST_USER_1), MAX_DURATION)
        val result: Option[User] = Await.result(repository.authenticate("fake-user", "password"), MAX_DURATION)
        result mustBe empty
      }
      "return empty if the username matches a user but the password is wrong" in {
        Await.result(repository.create(TEST_USER_1), MAX_DURATION)
        val result: Option[User] = Await.result(repository.authenticate("admin", "wrong-password"), MAX_DURATION)
        result mustBe empty
      }
      "return the user matching username and password" in {
        Await.result(repository.create(TEST_USER_1), MAX_DURATION)
        Await.result(repository.create(TEST_USER_2), MAX_DURATION)
        val result: Option[User] = Await.result(repository.authenticate("admin", "password"), MAX_DURATION)
        result must contain(userWithPasswordToUser(TEST_USER_1))
      }
    }

    "#list" should {
      "return an empty list if the database is empty" in {
        val result: Seq[User] = Await.result(repository.list, MAX_DURATION)
        result mustBe empty
      }
      "return a list with a single user if the database contains a single user" in {
        Await.result(repository.create(TEST_USER_1), MAX_DURATION)
        val result: Seq[User] = Await.result(repository.list, MAX_DURATION)
        result mustEqual Seq[User](TEST_USER_1)
      }
      "return the list of the users in the database, ordered by username asc" in {
        Await.result(repository.create(TEST_USER_2), MAX_DURATION)
        Await.result(repository.create(TEST_USER_1), MAX_DURATION)
        val result: Seq[User] = Await.result(repository.list, MAX_DURATION)
        result mustEqual Seq[User](TEST_USER_1, TEST_USER_2)
      }
    }
  }
}