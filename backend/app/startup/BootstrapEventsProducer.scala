package startup

import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{RestartSink, RestartSource, Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.{Configuration, Logger}
import services.{Kafka, Swarm}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
final class BootstrapEventsProducer @Inject()(implicit ec: ExecutionContext, system: ActorSystem,
                                              config: Configuration, kafka: Kafka, swarm: Swarm) {

  // run akka streams in a separated Akka Actor
  private val materializer: ActorMaterializer = ActorMaterializer()

  // topic in Kafka where to read the events from
  private val topic: String = config.get[String]("kafka.topic")

  // connect to Docker Swarm API (automatic reconnection)
  val swarmSource: Source[String, NotUsed] = RestartSource.withBackoff(1.seconds, 2.seconds, 0.2) { () => swarm.streamEvents() }

  // connect to Kafka (automatic reconnection)
  val kafkaSink: Sink[ProducerRecord[Array[Byte], String], NotUsed] = RestartSink.withBackoff(1.seconds, 2.seconds, 0.2) { () => kafka.sink() }

  // stream events from Docker Swarm to Kafka
  // both source and sink implement automatic retry using Akka supervision
  swarmSource
    .watchTermination()((_, _) => Logger.error("Stream from Docker Swarm APIs to Kafka was terminated! [this should not happen]"))
    .map { event =>
      Logger.info(s"[Kafka pipeline] Raw event: $event")
      event
    }
    .map(new ProducerRecord[Array[Byte], String](topic, _))
    .runWith(kafkaSink)(materializer)

  // stream to console
  swarmSource.runForeach {
    event => Logger.debug(s"[Console pipeline] Raw event: $event")
  }(materializer)
}
