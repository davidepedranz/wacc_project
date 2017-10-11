package repositories

import models.{Credentials, User, UserWithPassword}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}
import scalaz.\/

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
    users.foreach(user => {
      val result: \/[DuplicateUser, Unit] = Await.result(repository.create(user), MAX_DURATION)
      result.isRight mustBe true
    })
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
        val result: Option[User] = Await.result(repository.authenticate(Credentials("fake-user", "password")), MAX_DURATION)
        result mustBe empty
      }
      "return empty if the username matches a user but the password is wrong" in {
        createUsers(TEST_USER_1)
        val result: Option[User] = Await.result(repository.authenticate(Credentials(TEST_USER_1.username, "wrong-password")), MAX_DURATION)
        result mustBe empty
      }
      "return the user matching username and password" in {
        createUsers(TEST_USER_1, TEST_USER_2)
        val result: Option[User] = Await.result(repository.authenticate(Credentials(TEST_USER_1.username, TEST_USER_1.password)), MAX_DURATION)
        result must contain(userWithPasswordToUser(TEST_USER_1))
      }
    }

    "#create" should {
      "create the user in the repository" in {
        createUsers(TEST_USER_1)
        assertExists(TEST_USER_1)
      }
      "do not create the user in the repository if there is already one with the same username" in {
        createUsers(TEST_USER_1)
        val result: \/[DuplicateUser, Unit] = Await.result(repository.create(TEST_USER_1), MAX_DURATION)
        result.isLeft mustBe true
        result.toEither.left.toOption.value mustBe DuplicateUser()
      }
    }

    "#list" should {
      "return an empty list if the repository is empty" in {
        val result: Seq[User] = Await.result(repository.list, MAX_DURATION)
        result mustBe empty
      }
      "return a list with a single user if the repository contains a single user" in {
        createUsers(TEST_USER_1)
        val result: Seq[User] = Await.result(repository.list, MAX_DURATION)
        result mustEqual Seq[User](TEST_USER_1)
      }
      "return the list of the users in the repository, ordered by username asc" in {
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
      "return an error if the user with the given username has already been removed" in {
        createUsers(TEST_USER_1)
        Await.result(repository.delete(TEST_USER_1.username), MAX_DURATION)
        val result: \/[UserNotFound, Unit] = Await.result(
          repository.delete(TEST_USER_1.username), MAX_DURATION
        )
        result.toEither mustBe 'left
        assertNotExists(TEST_USER_1)
      }
      "remove the user with the given username from the repository" in {
        createUsers(TEST_USER_1, TEST_USER_2)
        val result: \/[UserNotFound, Unit] = Await.result(
          repository.delete(TEST_USER_1.username), MAX_DURATION
        )
        result.toEither mustBe 'right
        assertNotExists(TEST_USER_1)
        assertExists(TEST_USER_2)
      }
    }

    "#addPermission" should {
      "return an error is the user does not exist" in {
        createUsers(TEST_USER_1)
        val result: \/[UserNotFound, Unit] = Await.result(
          repository.addPermission(TEST_USER_2.username, "some-new-permission"), MAX_DURATION
        )
        result.toEither mustBe 'left
        assertExists(TEST_USER_1)
      }
      "do nothing if the user already has the permission" in {
        createUsers(TEST_USER_1)
        val result: \/[UserNotFound, Unit] = Await.result(
          repository.addPermission(TEST_USER_1.username, TEST_USER_1.permissions.toSeq.head), MAX_DURATION
        )
        result.toEither mustBe 'right
        assertExists(TEST_USER_1)
      }
      "add the permission to the user" in {
        createUsers(TEST_USER_1)
        val result: \/[UserNotFound, Unit] = Await.result(
          repository.addPermission(TEST_USER_1.username, "some-new-permission"), MAX_DURATION
        )
        result.toEither mustBe 'right
        assertExists(TEST_USER_1.copy(permissions = TEST_USER_1.permissions + "some-new-permission"))
      }
    }

    "#removePermission" should {
      "return an error is the user does not exists" in {
        createUsers(TEST_USER_1)
        val result: \/[UserNotFound, Unit] = Await.result(
          repository.removePermission(TEST_USER_2.username, "some-new-permission"), MAX_DURATION
        )
        result.toEither mustBe 'left
        assertExists(TEST_USER_1)
      }
      "do nothing if the user does not have the permission" in {
        createUsers(TEST_USER_1)
        val result: \/[UserNotFound, Unit] = Await.result(
          repository.removePermission(TEST_USER_1.username, "some-not-existing-permission"), MAX_DURATION
        )
        result.toEither mustBe 'right
        assertExists(TEST_USER_1)
      }
      "remove the permission from the user" in {
        createUsers(TEST_USER_1)
        val result: \/[UserNotFound, Unit] = Await.result(
          repository.removePermission(TEST_USER_1.username, TEST_USER_1.permissions.toSeq.head), MAX_DURATION
        )
        result.toEither mustBe 'right
        assertExists(TEST_USER_1.copy(permissions = TEST_USER_1.permissions - TEST_USER_1.permissions.toSeq.head))
      }
    }
  }
}
