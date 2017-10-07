package controllers

import javax.inject._

import be.objectify.deadbolt.scala.models.PatternType
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltHandler}
import models.{Permission, UserWithPassword}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.{DuplicateUser, UsersRepository}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/-}

@Singleton
class UsersController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, bodyParsers: PlayBodyParsers, handler: DeadboltHandler,
                                actionBuilders: ActionBuilders, usersRepository: UsersRepository) extends AbstractController(cc) {

  def create: Action[JsValue] = actionBuilders.PatternAction(value = Permission.USERS_WRITE, patternType = PatternType.EQUALITY).apply(parse.json) { req =>
    req.body.validate[UserWithPassword] match {
      case user: JsSuccess[UserWithPassword] => {
        usersRepository.create(user.value).map {
          case \/-(_) => Ok
          case -\/(_: DuplicateUser) => Conflict
        }.recover {
          case _ => ServiceUnavailable
        }
      }
      case error: JsError => {
        Future.successful(BadRequest(JsError.toJson(error)))
      }
    }
  }

  def list: Action[AnyContent] = actionBuilders.PatternAction(value = Permission.USERS_READ, patternType = PatternType.EQUALITY).defaultHandler() {
    usersRepository.list.map { users =>
      Ok(Json.toJson(users))
    }.recover {
      case _ => ServiceUnavailable
    }
  }

}
