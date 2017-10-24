import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.RestartSource
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.Configuration
import services.{Kafka, Swarm}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
final class BootstrapEventsProducer @Inject()(implicit ec: ExecutionContext, materializer: Materializer,
                                              system: ActorSystem, config: Configuration, kafka: Kafka, swarm: Swarm) {

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
}
