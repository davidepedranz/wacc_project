package controllers

import javax.inject._

import authentication.Authentication
import models.{Credentials, Token, User}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.UsersRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
final class LoginController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, users: UsersRepository,
                                      authentication: Authentication) extends AbstractController(cc) {

  /**
    * Authenticates the user using username + password as credentials.
    *
    * @return A response that contains the token if the authentication is successful.
    */
  def login: Action[JsValue] = Action.async(parse.json) { req =>
    req.body.validate[Credentials] match {
      case error: JsError => Future(BadRequest(JsError.toJson(error)))
      case credentials: JsSuccess[Credentials] => users.authenticate(credentials.value).map {
        case Some(user: User) => Ok(Json.toJson(Token(authentication.generateToken(user.username), user)))
        case None => Unauthorized
      }
    }
  }
}
