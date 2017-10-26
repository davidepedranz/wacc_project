package workers

import akka.NotUsed
import akka.kafka.ProducerMessage
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RestartSink, RestartSource, RunnableGraph, Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Configuration, Environment, Logger}
import services.{Kafka, Swarm}
import startup.StartupModule

import scala.concurrent.duration._

object OldWorker extends App {

  // bootstrap a new application
  val config = Configuration.load(Environment.simple())
  val app = GuiceApplicationBuilder(configuration = config)
    .disable[StartupModule]
    .build()

  // get dependencies
  val swarm = app.injector.instanceOf[Swarm]
  val kafka = app.injector.instanceOf[Kafka]


  // topic in Kafka where to read the events from
  val topic: String = config.get[String]("kafka.topic")


  // connect to Docker Swarm API (automatic reconnection)
  val swarmSource: Source[String, NotUsed] = RestartSource.withBackoff(1.seconds, 2.seconds, 0.5) { () => swarm.streamEvents() }

  // connect to Kafka (automatic reconnection)
  val kafkaSink: Sink[ProducerRecord[Array[Byte], String], NotUsed] = RestartSink.withBackoff(1.seconds, 2.seconds, 0.5) { () => kafka.sink() }

  // connect to Kafka
  val kafkaFlow: Flow[ProducerMessage.Message[Array[Byte], String, None.type], ProducerMessage.Result[Array[Byte], String, None.type], NotUsed] = kafka.flow()

  //  log to the console
  val consoleSink: Sink[String, NotUsed] = RestartSink.withBackoff(1.seconds, 2.seconds, 0.5) {
    () => Sink.foreach(event => Logger.info(s"[Console] Docker Swarm event: $event"))
  }

  // flow: transform events into messages for Kafka
  val toKafkaRecord: Flow[String, ProducerRecord[Array[Byte], String], NotUsed] = Flow[String]
    .map(new ProducerRecord[Array[Byte], String](topic, _))
  val toKafkaMessage: Flow[ProducerRecord[Array[Byte], String], ProducerMessage.Message[Array[Byte], String, None.type], NotUsed] = Flow[ProducerRecord[Array[Byte], String]]
    .map(ProducerMessage.Message(_, None))

  val logger: Flow[String, String, NotUsed] = Flow[String]
    .map { event =>
      Logger.info(s"[Kafka] Docker Swarm event: $event")
      event
    }

  // define the final flow
  val stream: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._
    val broadcast = builder.add(Broadcast[String](2))

    swarmSource ~> broadcast ~> logger ~> toKafkaRecord ~> kafkaSink
    broadcast ~> consoleSink

    ClosedShape
  })

  // finally, materialize and run
  stream.run()(app.materializer)
}