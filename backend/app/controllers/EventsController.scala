package controllers

import java.util.{Calendar, Date, TimeZone}
import javax.inject._

import akka.NotUsed
import akka.stream.scaladsl.{Flow, RestartSource, Sink, Source}
import models.Event
import play.Logger
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import repositories.EventsRepository
import services.Kafka

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class EventsController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, config: Configuration,
                                 kafka: Kafka, repository: EventsRepository) extends AbstractController(cc) {

  private implicit val messageFlowTransformer: MessageFlowTransformer[String, Event] = {
    MessageFlowTransformer.jsonMessageFlowTransformer[String, Event]
  }

  private val topic: String = config.get[String]("kafka.topic")

  /**
    * Action that creates a WebSocket to stream events from the backend. Old events will be read
    * from the Cassandra store, while new events are read from Kafka. Streamed events may be
    * out-of-order and / or duplicated. It is a responsibility of the client to filter them out.
    */
  def socket: WebSocket = WebSocket.acceptOrResult[String, Event] { _ =>
    // TODO: parse token + authenticate... return Left if token / permission wrong

    // read old events from Cassandra
    val oldEvents: Source[Event, NotUsed] =
      RestartSource.withBackoff(minBackoff = 1.seconds, maxBackoff = 2.seconds, randomFactor = 0.2) { () => Source.fromFuture(getOldEvents) }
        .mapConcat(identity[List[Event]])

    // connect to Kafka to get live streaming
    val liveEvents: Source[Event, _] =
      RestartSource.withBackoff(minBackoff = 1.seconds, maxBackoff = 2.seconds, randomFactor = 0.2) { () => kafka.source(topic) }
        .map(raw => Json.parse(raw.value).as[Event])
        .recover {
          case ex =>
            Logger.warn(s"Cannot parse string from Kafka to an Event object", ex)
            Event(new Date(System.currentTimeMillis()), 2L, "fake", "fake", "fake")
        }

    // combine events from Cassandra to updates from Kafka
    val out: Source[Event, NotUsed] = oldEvents.concat(liveEvents)

    // client -> input [ignored] -> ... -> kafka -> events -> out -> client
    Future(Right(Flow.fromSinkAndSource(Sink.ignore, out)))
  }

  private def getOldEvents: Future[List[Event]] = {

    // TODO: connect to Cassandra to get the last events
    val cal = Calendar.getInstance(TimeZone.getTimeZone("ECT"))
    cal.add(Calendar.DATE, -1)
    val date = cal.getTime
    cal.add(Calendar.DATE, +1)
    val date1 = cal.getTime

    // TODO: please use better names
    for {
      olds <- repository.readByDate(date)
      olds1 <- repository.readByDate(date1)
    } yield {
      olds ::: olds1
    }
  }
}
