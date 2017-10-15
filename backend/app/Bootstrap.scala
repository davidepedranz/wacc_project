import javax.inject._

import akka.stream.Materializer
import models.UserWithPassword
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Logger}
import repositories.UsersRepository
import services.{Kafka, Swarm}

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

@Singleton
final class Bootstrap @Inject()(implicit ec: ExecutionContext, lifecycle: ApplicationLifecycle, materializer: Materializer,
                                config: Configuration, usersRepository: UsersRepository, kafka: Kafka, swarm: Swarm) {

  // if not user is present, create a default one
  usersRepository.list.map { users =>
    if (users.isEmpty) {
      Logger.warn("The database of users is empty... creating the default user.")
      usersRepository.create(UserWithPassword.DEFAULT_USER)
    } else {
      Logger.info("The database of users is not empty... skip creating the default user.")
    }
  }

  // connect to swarm and stream events to Kafka (automatic errors retry)
  private val topic: String = config.get[String]("kafka.topic")
  swarm.streamEvents.map { source =>
    source
      .map {
        elem => {
          Logger.debug("[Docker Swarm] -> " + elem)
          new ProducerRecord[Array[Byte], String](topic, elem)
        }
      }
      .to(kafka.sink)
      .run()
  }
}
