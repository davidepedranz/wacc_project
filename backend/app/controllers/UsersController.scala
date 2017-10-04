package controllers

import javax.inject._

import models.{User, UsersRepository}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UsersController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, usersRepository: UsersRepository) extends AbstractController(cc) {

  def list = Action.async {
    usersRepository.list.map { users =>
      Ok(Json.toJson(users))
    }
  }

  def create = Action.async(parse.json) { req =>
    req.body.validate[User] match {
      case user: JsSuccess[User] => Future.successful(Ok)
      case error: JsError => Future.successful(BadRequest(JsError.toJson(error)))
    }
  }

}
