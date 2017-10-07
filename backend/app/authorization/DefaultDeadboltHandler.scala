package authorization

import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler}
import models.{Permission, Subject, User}
import play.api.mvc.{Request, Result, Results}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultDeadboltHandler extends DeadboltHandler with Results {

  // TODO: make sure the token in present
  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future.successful(None)

  // not required since we do not use "dynamic constraint types"
  override def getDynamicResourceHandler[A](request: Request[A]): Future[None.type] = Future.successful(None)

  // TODO; replace with the real implementation
  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = Future[Option[Subject]] {
    Option(
      Subject.fromUser(
        User(
          "admin",
          Set(Permission.USERS_READ, Permission.USERS_WRITE)
        )
      )
    )
  }

  // if the authorization fails, simply return HTTP 403 Forbidden
  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = Future.successful(Forbidden)

}
