package startup

import javax.inject.{Inject, Singleton}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RestartSink, RestartSource, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.{Configuration, Logger}
import services.{Kafka, Swarm}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
final class BootstrapEventsProducer @Inject()(implicit ec: ExecutionContext, config: Configuration, kafka: Kafka, swarm: Swarm) {

  // run akka streams in a separated Akka Actor
  private val actorSystem: ActorSystem = ActorSystem("EventsStreaming")
  private val materializer: ActorMaterializer = ActorMaterializer()(actorSystem)

  // topic in Kafka where to read the events from
  private val topic: String = config.get[String]("kafka.topic")

  //  // connect to Docker Swarm API (automatic reconnection)
  //  val swarmSource: Source[String, NotUsed] = RestartSource.withBackoff(1.seconds, 2.seconds, 0.2) { () => swarm.streamEvents() }
  //
  //  // connect to Kafka (automatic reconnection)
  //  val kafkaSink: Sink[ProducerRecord[Array[Byte], String], NotUsed] = RestartSink.withBackoff(1.seconds, 2.seconds, 0.2) { () => kafka.sink() }
  //
  //  // stream events from Docker Swarm to Kafka
  //  // both source and sink implement automatic retry using Akka supervision
  //  swarmSource
  //    .watchTermination()((_, _) => Logger.error("Stream from Docker Swarm APIs to Kafka was terminated! [this should not happen]"))
  //    .map { event =>
  //      Logger.info(s"[Kafka pipeline] Raw event: $event")
  //      event
  //    }
  //    .map(new ProducerRecord[Array[Byte], String](topic, _))
  //    .runWith(kafkaSink)(materializer1)
  //
  //  // stream to console
  //  swarmSource
  //    .watchTermination()((_, _) => Logger.error("Stream from Docker Swarm APIs to Console was terminated! [this should not happen]"))
  //    .runForeach {
  //      event => Logger.debug(s"[Console pipeline] Raw event: $event")
  //    }(materializer2)


  // connect to Docker Swarm API (automatic reconnection)
  val swarmSource: Source[String, NotUsed] = RestartSource.withBackoff(1.seconds, 2.seconds, 0.5) { () => swarm.streamEvents() }

  // connect to Kafka (automatic reconnection)
  val kafkaSink: Sink[ProducerRecord[Array[Byte], String], NotUsed] = RestartSink.withBackoff(1.seconds, 2.seconds, 0.5) { () => kafka.sink() }

  // log to the console
  val consoleSink: Sink[String, NotUsed] = RestartSink.withBackoff(1.seconds, 2.seconds, 0.5) {
    () => Sink.foreach(event => Logger.info(s"[Console] Docker Swarm event: $event"))
  }

  // flow: transform events into messages for Kafka
  val flow: Flow[String, ProducerRecord[Array[Byte], String], NotUsed] = Flow[String]
    .map(new ProducerRecord[Array[Byte], String](topic, _))

  val logger: Flow[String, String, NotUsed] = Flow[String]
    .map { event =>
      Logger.info(s"[Kafka] Docker Swarm event: $event")
      event
    }

  // define the final flow
  val stream: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val broadcast = builder.add(Broadcast[String](2))

    swarmSource ~> broadcast ~> logger ~> flow ~> kafkaSink
    broadcast ~> consoleSink

    ClosedShape
  })

  // finally, materialize and run
  stream.run()(materializer)
}
