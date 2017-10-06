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

final class MongoUsersRepositorySpec extends PlaySpec with GuiceOneAppPerTest with BeforeAndAfterEach {
  private val injector: Injector = new GuiceApplicationBuilder().build.injector
  private val mongo: ReactiveMongoApi = injector.instanceOf[ReactiveMongoApi]
  private val repository: UsersRepository = injector.instanceOf[MongoUsersRepository]

  private val MAX_DURATION: FiniteDuration = 2 seconds

  private val TEST_USER_1 = UserWithPassword("admin", "password", Set("admin"))
  private val TEST_USER_2 = UserWithPassword("test", "password", Set("read-a", "write-b"))

  private implicit def userWithPasswordToUser(user: UserWithPassword): User = User(
    username = user.username,
    permissions = user.permissions
  )

  private def createUsers(users: UserWithPassword*): Unit = {
    users.foreach(user => Await.result(repository.create(user), MAX_DURATION))
  }

  private def assertExists(user: UserWithPassword) = {
    val result: Option[User] = Await.result(repository.read(user.username), MAX_DURATION)
    result mustBe defined
    result must contain(userWithPasswordToUser(user))
  }

  private def assertNotExists(user: UserWithPassword) = {
    val result: Option[User] = Await.result(repository.read(user.username), MAX_DURATION)
    result mustBe empty
  }

  override protected def beforeEach(): Unit = {
    Await.ready(mongo.database.map(db => db.drop()), MAX_DURATION)
    System.out.println("Drop database in MongoDB... [done]")
  }

  "MongoUsersRepository" when {

    "#authenticate" should {
      "return empty if the username does not match any user" in {
        createUsers(TEST_USER_1)
        val result: Option[User] = Await.result(repository.authenticate("fake-user", "password"), MAX_DURATION)
        result mustBe empty
      }
      "return empty if the username matches a user but the password is wrong" in {
        createUsers(TEST_USER_1)
        val result: Option[User] = Await.result(repository.authenticate(TEST_USER_1.username, "wrong-password"), MAX_DURATION)
        result mustBe empty
      }
      "return the user matching username and password" in {
        createUsers(TEST_USER_1, TEST_USER_2)
        val result: Option[User] = Await.result(repository.authenticate(TEST_USER_1.username, TEST_USER_1.password), MAX_DURATION)
        result must contain(userWithPasswordToUser(TEST_USER_1))
      }
    }

    "#list" should {
      "return an empty list if the database is empty" in {
        val result: Seq[User] = Await.result(repository.list, MAX_DURATION)
        result mustBe empty
      }
      "return a list with a single user if the database contains a single user" in {
        createUsers(TEST_USER_1)
        val result: Seq[User] = Await.result(repository.list, MAX_DURATION)
        result mustEqual Seq[User](TEST_USER_1)
      }
      "return the list of the users in the database, ordered by username asc" in {
        createUsers(TEST_USER_1, TEST_USER_2)
        val result: Seq[User] = Await.result(repository.list, MAX_DURATION)
        result mustEqual Seq[User](TEST_USER_1, TEST_USER_2)
      }
    }

    "#read" should {
      "return empty if there is no user with the given username" in {
        createUsers(TEST_USER_1)
        assertNotExists(TEST_USER_2)
      }
      "return the user with the given username" in {
        createUsers(TEST_USER_1)
        assertExists(TEST_USER_1)
      }
    }

    "#delete" should {
      "do nothing if there is no user with the given username" in {
        createUsers(TEST_USER_1)
        Await.result(repository.delete("not-existing-user"), MAX_DURATION)
        assertExists(TEST_USER_1)
      }
      "do nothing if the user with the given username has already been remove" in {
        createUsers(TEST_USER_1)
        Await.result(repository.delete(TEST_USER_1.username), MAX_DURATION)
        Await.result(repository.delete(TEST_USER_1.username), MAX_DURATION)
        assertNotExists(TEST_USER_1)
      }
      "remove the user with the given username from the repository" in {
        createUsers(TEST_USER_1, TEST_USER_2)
        Await.result(repository.delete(TEST_USER_1.username), MAX_DURATION)
        assertNotExists(TEST_USER_1)
        assertExists(TEST_USER_2)
      }
    }
  }
}
