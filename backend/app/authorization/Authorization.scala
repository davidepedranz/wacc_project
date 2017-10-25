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

  private def parseAuthorizationHeader(header: String): Option[String] = {
    header.split("""\s""") match {
      case Array("Bearer", token) => Option(token)
      case _ ⇒ None
    }
  }

  // make sure the token in present (NB: we must return None as a result to apply this step)
  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = {
    request.headers.get("Authorization") match {
      case Some(_: String) => Future(None)
      case None => Future(Option(Unauthorized))
    }
  }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    val token: String = request.headers.get("Authorization").flatMap(parseAuthorizationHeader).getOrElse[String]("")
    authentication.parseToken(token) match {
      case Failure(_) => Future(Option.empty)
      case Success(username: String) => usersRepository.read(username).map {
        case Some(user) => Option(Subject.fromUser(user))
        case None => Option.empty
      }
    }
  }

  // if the authorization fails, simply return HTTP 403 Forbidden
  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = Future(Forbidden)

  // not required since we do not use "dynamic constraint types"
  override def getDynamicResourceHandler[A](request: Request[A]): Future[None.type] = Future(None)
}
