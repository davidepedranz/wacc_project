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

class DefaultDeadboltHandler @Inject()(authentication: Authentication, usersRepository: UsersRepository) extends DeadboltHandler with Results {

  // make sure the token in present
  // NB: we must return None as a result to apply this step
  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = {
    request.headers.get("Authorization") match {
      case Some(_: String) => Future.successful(None)
      case None => Future.successful(Option(Unauthorized))
    }
  }

  // TODO: find better way to convert from Future[Option[User]] to Future[Option[Subject]]
  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    val token: String = request.headers.get("Authorization").getOrElse[String]("")
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
