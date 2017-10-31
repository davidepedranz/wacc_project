package services

import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.stream.scaladsl.Source
import models.{Service, Task}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.{Configuration, Logger}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}


/**
  * Service to connect to the Docker Swarm APIs.
  */
@Singleton
final class Swarm @Inject()(implicit ec: ExecutionContext, config: Configuration, ws: WSClient) {

  // endpoint for the Docker Swarm APIs
  private val swarmUrl: String = config.get[String]("swarm.url")

  // request for streaming events
  private val events: WSRequest = ws
    .url(swarmUrl + "/events")
    .addQueryStringParameters("filters" -> Json.obj("type" -> Seq("service")).toString())
    .addHttpHeaders("Accept" -> "application/json")
    .withRequestTimeout(Duration.Inf)

  /**
    * Stream events from the Docker Swarm APIs as an Akka Stream.
    */
  def streamEvents(): Source[String, NotUsed] = {
    Logger.warn("Try to connect to Docker Swarm...")
    val future: Future[WSResponse] = events.stream
    future.onComplete {
      case Success(_) => Logger.info("Connected to Docker Swarm")
      case Failure(ex) => Logger.error(s"Connection to Docker Swarm closed for error -> ${ex.getMessage}")
    }
    Source.fromFuture(future)
      .flatMapConcat(_.bodyAsSource.map(_.utf8String))
      .map { s =>
        Logger.debug(s"Swarm Event: $s")
        s
      }
  }

  /**
    * Get the list of services running in the Docker Swarm cluster.
    */
  def getServices: Future[Seq[Service]] = {
    val services: Future[Seq[Service]] = ws.url(swarmUrl + "/services")
      .get()
      .map(response => response.json.as[Seq[Service]])

    val counts: Future[Map[String, Int]] = ws.url(swarmUrl + "/tasks")
      .get()
      .map(response => response.json.as[Seq[Task]])
      .map(tasks => tasks.filter(_.status == "running").map(_.service).groupBy(identity).mapValues(_.size))

    for {
      services <- services
      counts <- counts
    } yield {
      services.map(service => service.copy(current = counts.getOrElse(service.id, 0)))
    }
  }

  /**
    * Create a new service in the Docker Swarm cluster.
    */
  def createService(request: JsValue): Future[Boolean] = {
    ws.url(swarmUrl + "/services/create")
      .withHttpHeaders("Accept" -> "application/json")
      .post(request)
      .map(response => response.body.contains("ID"))
  }

  /**
    * Update an existing service in the Docker Swarm cluster.
    */
  def updateService(id: String, request: JsValue): Future[WSResponse] = {
    ws.url(swarmUrl + "/services/" + id)
      .get()
      .flatMap { response =>
        val version: String = Json.stringify((Json.parse(response.body) \ "Version" \ "Index").get)
        ws.url(swarmUrl + "/services/" + id + "/update?version=" + version)
          .withHttpHeaders("Accept" -> "application/json")
          .post(request)
      }
  }

  /**
    * Delete a service from the Docker Swarm cluster.
    */
  def deleteService(id: String): Future[WSResponse] = {
    ws.url(swarmUrl + "/services/" + id).delete()
  }
}
