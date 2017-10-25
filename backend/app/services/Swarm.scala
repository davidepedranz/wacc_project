package services

import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.actor.{ActorSystem, Scheduler}
import akka.stream.scaladsl.Source
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.{Configuration, Logger}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

@Singleton
final class Swarm @Inject()(implicit ec: ExecutionContext, config: Configuration, actorSystem: ActorSystem, ws: WSClient) {
  implicit val s: Scheduler = actorSystem.scheduler

  private val swarmUrl: String = config.get[String]("docker_host")

  val events: WSRequest = ws.url(swarmUrl + "/events")
    .addQueryStringParameters("filters" -> Json.obj("type" -> Seq("service")).toString())
    .addHttpHeaders("Accept" -> "application/json")
    .withRequestTimeout(Duration.Inf)

  // http://loicdescotte.github.io/posts/play25-akka-streams/
  def streamEvents(): Source[String, NotUsed] = {
    Logger.warn("Try to connect to Docker Swarm...")
    val future: Future[WSResponse] = events.stream
    future.onComplete {
      case Success(_) => Logger.info("Connected to Docker Swarm")
      case Failure(ex) => Logger.error(s"Connection to Docker Swarm closed for error -> ${ex.getMessage}")
    }
    Source.fromFuture(future)
      .flatMapConcat(_.bodyAsSource.map(_.utf8String))
      .map(raw => raw.trim)
      .map { s =>
        Logger.debug(s"Swarm Event: $s")
        s
      }
  }
}
