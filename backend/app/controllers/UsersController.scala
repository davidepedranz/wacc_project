package controllers

import javax.inject._

import be.objectify.deadbolt.scala.models.PatternType
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltHandler}
import models.{Permission, UserWithPassword}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.{DuplicateUser, UserNotFound, UsersRepository}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/-}

@Singleton
class UsersController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, bodyParsers: PlayBodyParsers,
                                handler: DeadboltHandler, actionBuilders: ActionBuilders,
                                usersRepository: UsersRepository) extends AbstractController(cc) {

  def create: Action[JsValue] = actionBuilders.PatternAction(value = Permission.USERS_WRITE,
    patternType = PatternType.EQUALITY).apply(parse.json) { req =>
    req.body.validate[UserWithPassword] match {
      case error: JsError => Future.successful(BadRequest(JsError.toJson(error)))
      case user: JsSuccess[UserWithPassword] => usersRepository.create(user.value).map {
        case \/-(_: Unit) => Ok
        case -\/(_: DuplicateUser) => Conflict
      }.recover {
        case _ => ServiceUnavailable
      }
    }
  }

  def list: Action[AnyContent] = actionBuilders.PatternAction(value = Permission.USERS_READ,
    patternType = PatternType.EQUALITY).defaultHandler() {
    usersRepository.list.map { users =>
      Ok(Json.toJson(users))
    }.recover {
      case _ => ServiceUnavailable
    }
  }

  def delete(username: String): Action[AnyContent] = actionBuilders.PatternAction(value = Permission.USERS_WRITE,
    patternType = PatternType.EQUALITY).defaultHandler() { request =>
    request.subject.get.identifier match {
      // make sure we do not delete our user
      case `username` => Future(Forbidden)
      case _ => usersRepository.delete(username).map {
        case \/-(_: Unit) => NoContent
        case -\/(_: UserNotFound) => NotFound
      }.recover {
        case _ => ServiceUnavailable
      }
    }
  }
}
