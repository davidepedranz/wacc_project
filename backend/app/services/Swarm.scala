package services

import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.actor.{ActorSystem, Scheduler}
import akka.stream.scaladsl.Source
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
final class Swarm @Inject()(implicit ec: ExecutionContext, config: Configuration, actorSystem: ActorSystem, ws: WSClient) {
  implicit val s: Scheduler = actorSystem.scheduler

  private val swarmUrl: String = config.get[String]("docker_host")

  val events: WSRequest = ws.url(swarmUrl + "/events")
    .addQueryStringParameters("filters" -> Json.obj("type" -> Seq("service")).toString())
    .addHttpHeaders("Accept" -> "application/json")
    .withRequestTimeout(Duration.Inf)

  // http://loicdescotte.github.io/posts/play25-akka-streams/
  def streamEvents: Source[String, NotUsed] = {
    Logger.info("Connecting to Docker Swarm...")
    Source.fromFuture(events.stream)
      .flatMapConcat(_.bodyAsSource.map(_.utf8String))
      .map { s =>
        Logger.debug(s"Swarm Event: $s")
        s
      }
  }
}
