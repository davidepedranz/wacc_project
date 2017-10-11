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
final class UsersController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, bodyParsers: PlayBodyParsers,
                                      handler: DeadboltHandler, actionBuilders: ActionBuilders,
                                      usersRepository: UsersRepository) extends AbstractController(cc) {

  private val usersReadPermission: actionBuilders.PatternAction.PatternActionBuilder = {
    actionBuilders.PatternAction(value = Permission.USERS_READ, patternType = PatternType.EQUALITY)
  }

  private val usersWritePermission: actionBuilders.PatternAction.PatternActionBuilder = {
    actionBuilders.PatternAction(value = Permission.USERS_WRITE, patternType = PatternType.EQUALITY)
  }

  // TODO: add errors descriptions as json?

  def create: Action[JsValue] = usersWritePermission.apply(parse.json) { req =>
    req.body.validate[UserWithPassword] match {
      case error: JsError => Future(BadRequest(JsError.toJson(error)))
      case user: JsSuccess[UserWithPassword] => usersRepository.create(user.value).map {
        case \/-(_: Unit) => Created
        case -\/(_: DuplicateUser) => Conflict
      }.recover {
        case _ => ServiceUnavailable
      }
    }
  }

  def list: Action[AnyContent] = usersReadPermission.defaultHandler() {
    usersRepository.list.map { users =>
      Ok(Json.toJson(users))
    }.recover {
      case _ => ServiceUnavailable
    }
  }

  def delete(username: String): Action[AnyContent] = usersWritePermission.defaultHandler() { request =>
    request.subject.get.identifier match {
      // make sure a user can not delete itself
      case `username` => Future(Forbidden)
      case _ => usersRepository.delete(username).map {
        case \/-(_: Unit) => NoContent
        case -\/(_: UserNotFound) => NotFound
      }.recover {
        case _ => ServiceUnavailable
      }
    }
  }

  def addPermission(username: String, permission: String): Action[AnyContent] = usersWritePermission.defaultHandler() {
    // noinspection SimplifyBooleanMatch
    Permission.validate(permission) match {
      case false => Future(BadRequest)
      case true => usersRepository.addPermission(username, permission).map {
        case \/-(_: Unit) => Created
        case -\/(_: UserNotFound) => NotFound
      }.recover {
        case _ => ServiceUnavailable
      }
    }
  }

  def removePermission(username: String, permission: String): Action[AnyContent] = usersWritePermission.defaultHandler() { request =>
    (request.subject.get.identifier, permission) match {
      // make sure the user can not revoke its own write privilege
      case (`username`, Permission.USERS_WRITE) => Future(Forbidden)
      case _ => usersRepository.removePermission(username, permission).map {
        case \/-(_: Unit) => NoContent
        case -\/(_: UserNotFound) => NotFound
      }.recover {
        case _ => ServiceUnavailable
      }
    }
  }
}