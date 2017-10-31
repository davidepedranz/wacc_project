package workers

import akka.NotUsed
import akka.actor.Scheduler
import akka.kafka.ConsumerMessage.CommittableOffsetBatch
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerMessage, Subscriptions}
import akka.stream.scaladsl.{Flow, RestartSource, Sink}
import akka.stream.{ActorAttributes, Supervision}
import models.Event
import play.Logger
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.{Configuration, Environment}
import repositories.EventsRepository
import services.{Kafka, Retry}
import startup.StartupModule

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * Worker process to consume the events from the Kafka queue and store them in Cassandra for long term storage.
  * This object is run as a separate container, so it can be scaled independently from the backend.
  */
object EventsConsumerWorker extends App {

  // we use a fixed group name to consume events from Kafka, so each event is consumed only once
  private val KAFKA_CONSUMER_GROUP = "events-consumer-worker"

  // bootstrap a new application
  private val config = Configuration.load(Environment.simple())
  private val app = GuiceApplicationBuilder(configuration = config)
    .disable[StartupModule]
    .build()

  // get dependencies
  private implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  private implicit val s: Scheduler = app.actorSystem.scheduler
  private val kafka: Kafka = app.injector.instanceOf[Kafka]
  private val events: EventsRepository = app.injector.instanceOf[EventsRepository]

  // make sure Cassandra is ready
  private val repository: EventsRepository = app.injector.instanceOf[EventsRepository]
  Retry.periodically(
    repository.initialize(),
    1.seconds,
    (delay, ex) => Logger.error(s"Cannot connect to Cassandra to initialize the tables... (${ex.getMessage}). Retry in $delay...")
  )(ec, app.actorSystem.scheduler)

  // topic in Kafka where to read the events from
  private val topic: String = config.get[String]("kafka.topic")

  // connect to Kafka and stream the Docker Events (auto-reconnect)
  private val eventsSource = RestartSource.withBackoff(1.seconds, 2.seconds, 0.2) { () =>
    Logger.info(s"Kafka subscriber: topic=$topic, group=$KAFKA_CONSUMER_GROUP")
    Consumer.committableSource(kafka.consumerSettings(KAFKA_CONSUMER_GROUP), Subscriptions.topics(topic))
  }

  // parse the docker events -> skip malformed messages
  private val parseEvent: Flow[ConsumerMessage.CommittableMessage[Array[Byte], String], (ConsumerMessage.CommittableMessage[Array[Byte], String], Event), NotUsed] =
    Flow[ConsumerMessage.CommittableMessage[Array[Byte], String]]
      .map(msg => (msg, Json.parse(msg.record.value).as[Event]))
      .withAttributes(ActorAttributes.supervisionStrategy({ ex =>
        Logger.error(s"Cannot parse string from Kafka to an Event object... skip (do not save in Cassandra)", ex)
        Supervision.Resume
      }))

  // store events in Cassandra, retry forever if needed (at-least-one semantic)
  // we commit the events manually to Kafka to make sure to consume all of them!
  // we accept to process events in parallel, but the commits to Kafka are executed sequentially to avoid losses
  private val storeEvent = Flow[(ConsumerMessage.CommittableMessage[Array[Byte], String], Event)]
    .mapAsync(10) { pair =>
      val callback = (d: FiniteDuration, ex: Throwable) => Logger.error(s"Cannot connect to Cassandra (retry in $d) -> ${ex.getMessage}")
      Logger.debug(s"Got new event to store in Cassandra -> ${pair._2}")
      Retry.periodically(events.saveOrUpdate(pair._2), 2.seconds, callback).map(_ => pair._1)
    }
    .map(_.committableOffset)
    .batch(max = 10, first => CommittableOffsetBatch.empty.updated(first))((batch, elem) => batch.updated(elem))
    .map(_.commitScaladsl())


  // run the stream for the Docker Events: Kafka -> parse -> Cassandra
  eventsSource
    .via(parseEvent)
    .via(storeEvent)
    .runWith(Sink.ignore)(app.materializer)
    .onComplete {
      case Success(_) =>
        Logger.error(s"Events stream ended with success... this should never happen.")
        app.stop().map(_ => System.exit(1))
      case Failure(ex) =>
        // NB: this occurs when the worker is not able to connect to Cassandra upon start...
        // the external orchestrator will restart this worker upon failure
        Logger.error(s"End stream ended with failure -> ${ex.getMessage}", ex)
        app.stop().map(_ => System.exit(1))
    }
}