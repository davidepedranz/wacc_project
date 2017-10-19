import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.Materializer
import models.UserWithPassword
import org.apache.kafka.clients.producer.ProducerRecord
import play.Logger
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import repositories.UsersRepository
import services.{Kafka, Retry, Swarm}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps


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

  // connect to swarm and stream events to Kafka (automatic errors retry)
  private val topic: String = config.get[String]("kafka.topic")
  swarm.streamEvents.map { source =>
    source
      .map {
        elem => {
          Logger.debug("[Docker Swarm] write to kafka -> " + elem)
          new ProducerRecord[Array[Byte], String](topic, elem)
        }
      }
      .to(kafka.sink)
      .run()(materializer)
  }.recover {
    case ex =>
      Logger.error(s"Error streaming events from Docker Swarm.", ex)
  }
    .onComplete(_ => Logger.error("HTTP chunked streaming from Docker Swarm interrupted."))
}
