package controllers

import javax.inject._

import be.objectify.deadbolt.scala.models.PatternType
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions, DeadboltHandler}
import models.{Permission, Subject, UserWithPassword}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.{DuplicateUser, UserNotFound, UsersRepository}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/-}

/**
  * Controller that defines the logic to handle users in the systems (list, create, update, delete).
  */
@Singleton
final class UsersController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, bodyParsers: PlayBodyParsers,
                                      handler: DeadboltHandler, deadbolt: DeadboltActions, actionBuilders: ActionBuilders,
                                      usersRepository: UsersRepository) extends AbstractController(cc) {

  // builder for the permission: users.read
  private val usersReadPermission: actionBuilders.PatternAction.PatternActionBuilder = {
    actionBuilders.PatternAction(value = Permission.USERS_READ, patternType = PatternType.EQUALITY)
  }

  // builder for the permission: users.write
  private val usersWritePermission: actionBuilders.PatternAction.PatternActionBuilder = {
    actionBuilders.PatternAction(value = Permission.USERS_WRITE, patternType = PatternType.EQUALITY)
  }

  /**
    * Return the user that performed the request (used in the frontend to activate / deactivate buttons).
    */
  def me: Action[AnyContent] = deadbolt.SubjectPresent()() { req =>
    Future(Ok(Json.toJson(Subject.toUser(req.subject.get))))
  }

  /**
    * Create a new user.
    */
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

  /**
    * Get the list of users.
    */
  def list: Action[AnyContent] = usersReadPermission.defaultHandler() {
    usersRepository.list.map { users =>
      Ok(Json.toJson(users))
    }.recover {
      case _ => ServiceUnavailable
    }
  }

  /**
    * Delete a user. We also make sure that a user can not delete itself.
    */
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

  /**
    * Add a permission to a user.
    */
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

  /**
    * Remove a permission from a user. Make sure that a user can not revoke its own permission to read / write users.
    */
  def removePermission(username: String, permission: String): Action[AnyContent] = usersWritePermission.defaultHandler() { request =>
    (request.subject.get.identifier, permission) match {
      // make sure the user can not revoke its own write privilege
      case (`username`, Permission.USERS_READ) | (`username`, Permission.USERS_WRITE) => Future(Forbidden)
      case _ => usersRepository.removePermission(username, permission).map {
        case \/-(_: Unit) => NoContent
        case -\/(_: UserNotFound) => NotFound
      }.recover {
        case _ => ServiceUnavailable
      }
    }
  }
}