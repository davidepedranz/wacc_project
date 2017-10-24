package controllers

import java.util.{Calendar, Date, TimeZone}
import javax.inject._

import akka.stream.scaladsl.{Flow, Sink, Source}
import models.Event
import play.Logger
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import repositories.EventsRepository
import services.Kafka

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class EventsController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, config: Configuration,
                                 kafka: Kafka, eventsRepository: EventsRepository) extends AbstractController(cc) {

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
      olds <- eventsRepository.read(date)
      olds1 <- eventsRepository.read(date1)
    } yield {
      olds ::: olds1
    }
  }

  // TODO: this can not be sync!!!
  def oldEvents: List[Event] = Await.result(readOldEvents(), Duration.Inf)

  def socket: WebSocket = WebSocket.acceptOrResult[String, Event] { _ =>

    // TODO: test for killing kafka
    // connect to Kafka to get live streaming
    val liveEvents = kafka.source(topic)
      .map(raw => Json.parse(raw.value).as[Event])
      .recover {
        case ex =>
          Logger.warn(s"Cannot parse string from Kafka to an Event object", ex)
          Event(new Date(System.currentTimeMillis()), 2L, "fake", "fake", "fake")
      }

    // combine events from Cassandra to updates from Kafka
    val out = Source(oldEvents).concat(liveEvents)

    // client -> input [ignored] -> ... -> kafka -> events -> out -> client
    val flow = Flow.fromSinkAndSource(Sink.ignore, out)
    Future(Right(flow))
  }
}
