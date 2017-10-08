package controllers

import models.{Credentials, User}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.http.HeaderNames
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{Injector, bind}
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import repositories.UsersRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class LoginControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar with BeforeAndAfterEach {

  private val usersRepositoryMock: UsersRepository = spy(mock[UsersRepository])
  private val application = new GuiceApplicationBuilder()
    .overrides(bind[UsersRepository].toInstance(usersRepositoryMock))
    .build()
  private val injector: Injector = application.injector
  private val controller: LoginController = injector.instanceOf[LoginController]

  override protected def beforeEach(): Unit = {
    reset(usersRepositoryMock)
  }

  "LoginController" when {
    "POST /login" should {

      "return an error if the username is missing" in {
        val result: Future[Result] = controller.login().apply(FakeRequest(
          method = POST,
          uri = "/v1/login",
          headers = FakeHeaders(Seq(HeaderNames.HOST -> "localhost")),
          body = Json.parse(""" {"password": "password"} """)
        ))

        status(result) mustBe BAD_REQUEST
        verify(usersRepositoryMock, never()).authenticate(any())
      }

      "return an error if the password is missing" in {
        val result: Future[Result] = controller.login().apply(FakeRequest(
          method = POST,
          uri = "/v1/login",
          headers = FakeHeaders(Seq(HeaderNames.HOST -> "localhost")),
          body = Json.parse(""" {"username": "username"} """)
        ))

        status(result) mustBe BAD_REQUEST
        verify(usersRepositoryMock, never()).authenticate(any())
      }

      "return an error if the username or password are not correct" in {
        when(usersRepositoryMock.authenticate(Credentials("username", "password")))
          .thenReturn(Future(Option.empty))

        val result: Future[Result] = controller.login().apply(FakeRequest(
          method = POST,
          uri = "/v1/login",
          headers = FakeHeaders(Seq(HeaderNames.HOST -> "localhost")),
          body = Json.parse(""" {"username": "username", "password": "password"} """)
        ))

        status(result) mustBe UNAUTHORIZED
        verify(usersRepositoryMock, times(1)).authenticate(any())
      }

      "return a token if the username and password are correct" in {
        when(usersRepositoryMock.authenticate(Credentials("username", "password")))
          .thenReturn(Future(Option(User("username", Set.empty))))

        val result: Future[Result] = controller.login().apply(FakeRequest(
          method = POST,
          uri = "/v1/login",
          headers = FakeHeaders(Seq(HeaderNames.HOST -> "localhost")),
          body = Json.parse(""" {"username": "username", "password": "password"} """)
        ))

        status(result) mustBe OK
        verify(usersRepositoryMock, times(1)).authenticate(any())
      }
    }
  }
}
