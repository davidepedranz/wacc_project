import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.RestartSource
import models.Event
import play.api.libs.json.Json
import play.api.{Configuration, Logger}
import repositories.EventsRepository
import services.{Kafka, Swarm}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
final class BootstrapEventsConsumer @Inject()(implicit ec: ExecutionContext, system: ActorSystem, config: Configuration,
                                              kafka: Kafka, swarm: Swarm, eventsRepository: EventsRepository) {

  // custom materializer (the default one somehow does not materialize the stream of events from Kafka)
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  // topic for the Docker events in Kafka
  private val topic: String = config.get[String]("kafka.topic")

  // TODO: test for killing kafka
  // stream events from Kafka to Cassandra for durable storage
  RestartSource.withBackoff(minBackoff = 1.seconds, maxBackoff = 5.seconds, randomFactor = 0.2) { () => kafka.source(topic) }
    .runForeach { rawEvent =>
      val event = Json.parse(rawEvent.value).as[Event]
      eventsRepository.saveOrUpdate(event)
      Logger.debug(s"Saved event in Cassandra -> $event")
    }
    .recover {
      case ex => Logger.error("Cannot save some event in Cassandra.", ex)
    }
}
