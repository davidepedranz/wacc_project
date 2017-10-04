package controllers

import javax.inject._

import models.UserWithPassword
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import repositories.UsersRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UsersController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, usersRepository: UsersRepository) extends AbstractController(cc) {

  def list = Action.async {
    usersRepository.list.map { users =>
      Ok(Json.toJson(users))
    }
  }

  def create = Action.async(parse.json) { req =>
    req.body.validate[UserWithPassword] match {
      case user: JsSuccess[UserWithPassword] => usersRepository.create(user.value).map(result => Ok(result.toString))
      case error: JsError => Future.successful(BadRequest(JsError.toJson(error)))
    }
  }

}
