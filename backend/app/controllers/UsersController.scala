package controllers

import javax.inject._

import models.UserWithPassword
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import repositories.{DuplicateUser, UsersRepository}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/-}

@Singleton
class UsersController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, usersRepository: UsersRepository) extends AbstractController(cc) {

  def list = Action.async {
    usersRepository.list.map { users =>
      Ok(Json.toJson(users))
    }.recover {
      case _ => ServiceUnavailable
    }
  }

  // TODO: return always JSON as error?
  def create = Action.async(parse.json) { req =>
    req.body.validate[UserWithPassword] match {
      case user: JsSuccess[UserWithPassword] => {
        usersRepository.create(user.value).map {
          case \/-(_) => Ok
          case -\/(_: DuplicateUser) => Conflict("Username already taken")
        }.recover {
          case _ => ServiceUnavailable
        }
      }
      case error: JsError => {
        Future.successful(BadRequest(JsError.toJson(error)))
      }
    }
  }

}
