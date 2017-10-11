import javax.inject.{Inject, Provider, Singleton}

import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router

import scala.concurrent._

@Singleton
final class ErrorHandler @Inject()(env: Environment, config: Configuration, sourceMapper: OptionalSourceMapper, router: Provider[Router])
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Logger.debug(s"onClientError: statusCode = $statusCode, uri = ${request.uri}, message = $message")
    Future.successful {
      Results.Status(statusCode)(message)
    }
  }

  override protected def onDevServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    Future.successful(InternalServerError(Json.obj("exception" -> exception.toString)))
  }

  override protected def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    Future.successful(InternalServerError)
  }
}
