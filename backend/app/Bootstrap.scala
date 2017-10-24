import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.RestartSource
import models.{Event, UserWithPassword}
import org.apache.kafka.clients.producer.ProducerRecord
import play.Logger
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.Json
import repositories.{UsersRepository, eventDatabase}
import services.{Kafka, Retry, Swarm}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

// TODO: split in 2 classes!
@Singleton
final class Bootstrap @Inject()(implicit ec: ExecutionContext, lifecycle: ApplicationLifecycle,
                                materializer: Materializer, actorSystem: ActorSystem, config: Configuration,
                                usersRepository: UsersRepository, kafka: Kafka, swarm: Swarm) {

  // if not user is present, create a default one
  val callback: (FiniteDuration, Throwable) => Any = {
    (delay, ex) => Logger.error(s"Cannot connect to MongoDB to create the first user... (${ex.getMessage}). Retry in $delay...")
  }
  Retry.periodically(usersRepository.list, 3.seconds, callback)(ec, actorSystem.scheduler).map { users =>
    if (users.isEmpty) {
      Logger.warn("The database of users is empty... creating the default user.")
      usersRepository.create(UserWithPassword.DEFAULT_USER)
    } else {
      Logger.info("The database of users is not empty... skip creating the default user.")
    }
  }.recover {
    case ex => Logger.error("Can not connect to MongoDB.", ex)
  }

  // topic in Kafka where to read the events from
  private val topic: String = config.get[String]("kafka.topic")

  // stream events from Docker Swarm to Kafka
  //   --> connect to Docker Swarm API (wrap call in an Akka Stream)
  //   --> retry the stream if if fails (automatic reconnection)
  //   --> write events to Kafka
  RestartSource.withBackoff(minBackoff = 1.seconds, maxBackoff = 5.seconds, randomFactor = 0.2) { () => swarm.streamEvents }
    .map(new ProducerRecord[Array[Byte], String](topic, _))
    .to(kafka.sink)
    .run()(materializer)

  // stream events from Kafka to Cassandra for durable storage
  kafka.source(topic).map {
    rawEvent => {
      val event = Json.parse(rawEvent.value).as[Event]
      eventDatabase.saveOrUpdate(event)
    }
  }
}
