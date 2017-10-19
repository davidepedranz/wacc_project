package controllers

import javax.inject._

import akka.stream.javadsl.Source
import akka.stream.scaladsl.{Flow, Sink}
import models.Event
import play.Logger
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import services.Kafka

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class EventsController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, config: Configuration, kafka: Kafka) extends AbstractController(cc) {

  private implicit val messageFlowTransformer: MessageFlowTransformer[String, Event] = {
    MessageFlowTransformer.jsonMessageFlowTransformer[String, Event]
  }

  private val topic: String = config.get[String]("kafka.topic")

  def socket: WebSocket = WebSocket.acceptOrResult[String, Event] { _ =>

    // TODO: parse token + authenticate... return Left if token / permission wrong

    // TODO: connect to Cassandra to get the last events
    val oldEvents = Source.single(Event(System.currentTimeMillis(), "action", "service", "host"))

    // connect to Kafka to get live streaming
    val liveEvents = kafka.source(topic).map {
      x => Json.parse(x.value).as[Event]
    }.recover {
      case ex =>
        Logger.warn(s"Cannot parse string from Kafka to an Event object", ex)
        Event(0, "fake", "fake", "fake")
    }

    // client -> input [ignored] -> ... -> kafka -> events -> out -> client
    val flow = Flow.fromSinkAndSource(Sink.ignore, oldEvents.concat(liveEvents))
    Future(Right(flow))
  }
}
