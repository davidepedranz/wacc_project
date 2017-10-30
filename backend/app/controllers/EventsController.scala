package controllers

import java.util.{Calendar, Date, TimeZone}
import javax.inject._

import akka.NotUsed
import akka.stream.scaladsl.{Flow, RestartSource, Sink, Source}
import authentication.Authentication
import models.{Event, Permission}
import play.Logger
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import repositories.{EventsRepository, UsersRepository}
import services.Kafka

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

@Singleton
class EventsController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, config: Configuration,
                                 authentication: Authentication, users: UsersRepository,
                                 kafka: Kafka, events: EventsRepository) extends AbstractController(cc) {

  private val topic: String = config.get[String]("kafka.topic")

  private implicit val messageFlowTransformer: MessageFlowTransformer[String, Event] = {
    MessageFlowTransformer.jsonMessageFlowTransformer[String, Event]
  }

  /**
    * Action that creates a WebSocket to stream events from the backend. Old events will be read
    * from the Cassandra store, while new events are read from Kafka. Streamed events may be
    * out-of-order and / or duplicated. It is a responsibility of the client to filter them out.
    */
  def socket: WebSocket = WebSocket.acceptOrResult[String, Event] { request =>

    // unfortunately, Deadbolt does NOT support websockets...
    // let's make sure the user in authenticated and authorized to see the events
    // (we know, this is ugly, but we need all these checks for security)
    request.queryString.get("token").map(_.head) match {
      case None => Future(Left(Unauthorized))
      case Some(token) => authentication.parseToken(token) match {
        case Failure(_) => Future(Left(Forbidden))
        case Success(username) => users.read(username).map(_.map(_.permissions.contains(Permission.EVENTS))).map {
          case None | Some(false) => Left(Forbidden)
          case Some(true) => Right(eventsFlow)
        }
      }
    }
  }

  private def eventsFlow = {
    // TODO: parse token + authenticate... return Left if token / permission wrong

    // read old events from Cassandra
    val oldEvents: Source[Event, NotUsed] =
      RestartSource.withBackoff(minBackoff = 1.seconds, maxBackoff = 10.seconds, randomFactor = 0.2) { () => Source.fromFuture(getOldEvents) }
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

    // take both events from Cassandra to updates from Kafka
    // NB: we use merge here, NOT combine!
    val out: Source[Event, NotUsed] = oldEvents.merge(liveEvents)

    // client -> input [ignored] -> ... -> kafka -> events -> out -> client
    Flow.fromSinkAndSource(Sink.ignore, out)
  }


  /**
    * Read the old events from Cassandra.
    *
    * @return Future with the list of events from yesterday and today, at ECT time.
    */
  private def getOldEvents: Future[List[Event]] = {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("ECT"))
    val dateToday = calendar.getTime
    calendar.add(Calendar.DATE, -1)
    val dateYesterday = calendar.getTime

    for {
      oldEventsYesterday <- events.readByDate(dateYesterday)
      oldEventsToday <- events.readByDate(dateToday)
    } yield {
      oldEventsYesterday ::: oldEventsToday
    }
  }
}
