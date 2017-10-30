package authorization

import javax.inject.Inject

import authentication.Authentication
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler}
import models.Subject
import play.api.mvc.{Request, Result, Results}
import repositories.UsersRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Implement authorization using a permissions-based system.
  * Each action requires a given permission. The action is authorized if the user has the required permission.
  *
  * @param authentication  Trait to authenticate the user.
  * @param usersRepository Repository where to lookup users' permissions.
  */
final class Authorization @Inject()(authentication: Authentication, usersRepository: UsersRepository) extends DeadboltHandler with Results {

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = {
    // make sure the token in present (NB: we must return None as a result to apply this step)
    request.headers.get(authentication.AUTHENTICATION_HEADER) match {
      case Some(_: String) => Future(None)
      case None => Future(Option(Unauthorized))
    }
  }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {

    // extract the token
    val token: String = request.headers
      .get(authentication.AUTHENTICATION_HEADER)
      .flatMap(authentication.parseAuthorizationHeader)
      .getOrElse[String]("")

    // verify that the token is valid + match the permissions in the database
    authentication.parseToken(token) match {
      case Failure(_) => Future(Option.empty)
      case Success(username: String) => usersRepository.read(username).map {
        case Some(user) => Option(Subject.fromUser(user))
        case None => Option.empty
      }
    }
  }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    // if the authorization fails, simply return HTTP 403 Forbidden
    Future(Forbidden)
  }

  override def getDynamicResourceHandler[A](request: Request[A]): Future[None.type] = {
    // not required since we do not use "dynamic constraint types"
    // reference: https://deadbolt-scala.readme.io/docs/deadbolt-handlers
    Future(None)
  }
}
