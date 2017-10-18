package controllers

import java.util.{Calendar, Date, TimeZone}
import javax.inject._

import akka.stream.scaladsl.Source
import akka.stream.scaladsl.{Flow, Sink}
import models.Event
import play.api.{Configuration, Logger}
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import services.Kafka
import repositories.eventDatabase

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Try

@Singleton
class EventsController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, config: Configuration, kafka: Kafka) extends AbstractController(cc) {

  private implicit val messageFlowTransformer: MessageFlowTransformer[String, Event] = {
    MessageFlowTransformer.jsonMessageFlowTransformer[String, Event]
  }

  private val topic: String = config.get[String]("kafka.topic")

  def readOldEvents(): Future[List[Event]] = {
    // TODO: parse token + authenticate... return Left if token / permission wrong

    // TODO: connect to Cassandra to get the last events
    val cal = Calendar.getInstance(TimeZone.getTimeZone("ECT"))
    cal.add(Calendar.DATE, -1)
    val date = cal.getTime
    cal.add(Calendar.DATE, +1)
    val date1 = cal.getTime

    for {
      olds <- eventDatabase.read(date)
      olds1 <- eventDatabase.read(date1)
    } yield {
      olds:::olds1
    }
  }

  def getList: List[Event] = Await.result(readOldEvents(), Duration.Inf)

  def socket: WebSocket = WebSocket.acceptOrResult[String, Event] { _ =>
    // connect to Kafka to get live streaming
    val liveEvents = kafka.source(topic).map {
      x => {
        val event = Json.parse(x.value).as[Event]
        event
      }
    }
    val out = Source(getList).concat(liveEvents)

    // client -> input [ignored] -> ... -> kafka -> events -> out -> client
    val flow = Flow.fromSinkAndSource(Sink.ignore, out)
    Future(Right(flow))
  }
}
