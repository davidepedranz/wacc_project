package services

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorSystem, Scheduler}
import akka.stream.scaladsl.Source
import play.Logger
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}

import scala.concurrent.duration.{FiniteDuration, _}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
final class Swarm @Inject()(implicit ec: ExecutionContext, config: Configuration, actorSystem: ActorSystem, ws: WSClient) {
  implicit val s: Scheduler = actorSystem.scheduler

  private val swarmUrl: String = config.get[String]("docker_host")

  private val events: WSRequest = ws.url(swarmUrl + "/events")
    .addQueryStringParameters("filters" -> Json.obj("type" -> Seq("service")).toString())
    .addHttpHeaders("Accept" -> "application/json")

  def streamEvents: Future[Source[String, _]] = {

    // log the failed attempt to connect to Docker Swarm
    val callback: (FiniteDuration, Throwable) => Any = {
      (delay, ex) => Logger.error(s"Cannot connect to Docker Swarm (${ex.getMessage}). Retry in $delay...")
    }

    // connect to Docker Swarm and parse the results
    // we also handle automatic retrials in case Docker Swarm is not available for some reasons
    Retry.periodically(events.stream(), 5.seconds, callback).map { response =>
      Logger.info("Connected to Docker Swarm. Start to stream events to Kafka...")
      response.bodyAsSource.map(stream => stream.utf8String)
    }.recoverWith {
      case ex =>
        Logger.error(s"Error streaming events from Docker Swarm.", ex)
        Future.failed(new Exception("A prettier error message", ex))
    }
  }
}
