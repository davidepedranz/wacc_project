package services

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorSystem, Scheduler}
import akka.pattern.after
import akka.stream.scaladsl.Source
import play.Logger
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}

import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
final class Swarm @Inject()(implicit ec: ExecutionContext, ws: WSClient, actorSystem: ActorSystem) {
  implicit val s: Scheduler = actorSystem.scheduler

  private def retryPeriodically[T](f: => Future[T], delay: FiniteDuration, callback: (FiniteDuration, Throwable) => Any)
                                  (implicit ec: ExecutionContext, s: Scheduler): Future[T] = {
    f recoverWith {
      case ex =>
        callback(delay, ex)
        after(delay, s)(retryPeriodically(f, delay, callback))
    }
  }

  private val events: WSRequest = ws.url("http://localhost:2375/events")
    .addQueryStringParameters("filters" -> Json.obj("type" -> Seq("service")).toString())
    .addHttpHeaders("Accept" -> "application/json")

  def streamEvents: Future[Source[String, _]] = {

    // log the failed attempt to connect to Docker Swarm
    val callback: (FiniteDuration, Throwable) => Any = {
      (delay, ex) => Logger.error(s"Cannot connect to Docker Swarm (${ex.getMessage}). Retry in $delay...")
    }

    // connect to Docker Swarm and parse the results
    // we also handle automatic retrials in case Docker Swarm is not available for some reasons
    retryPeriodically(events.stream(), 5.seconds, callback).map { response =>
      response.bodyAsSource.map(stream => stream.utf8String)
    }
  }
}
