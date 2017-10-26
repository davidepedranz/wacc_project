package workers

import akka.NotUsed
import akka.kafka.ProducerMessage
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Flow, GraphDSL, RestartFlow, RestartSink, RestartSource, RunnableGraph, Sink, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Configuration, Environment, Logger}
import services.{Kafka, Swarm}
import startup.StartupModule

import scala.concurrent.duration._

object Worker extends App {

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

  // connect to Docker Swarm API
  val swarmSource: Source[String, NotUsed] = RestartSource.withBackoff(1.seconds, 2.seconds, 0.5) {
    () => swarm.streamEvents()
  }

  // flow that can be used to log the flowing messages
  val logger: Flow[String, String, NotUsed] = Flow[String]
    .map { event =>
      Logger.info(s"[Before Kafka] Docker Swarm event: $event")
      event
    }

  // flow: transform events into messages for Kafka
  val toKafkaRecord: Flow[String, ProducerRecord[Array[Byte], String], NotUsed] = Flow[String]
    .map(new ProducerRecord[Array[Byte], String](topic, _))
  val toKafkaMessage: Flow[ProducerRecord[Array[Byte], String], ProducerMessage.Message[Array[Byte], String, None.type], NotUsed] = Flow[ProducerRecord[Array[Byte], String]]
    .map(ProducerMessage.Message(_, None))

  // connect to Kafka
  val kafkaFlow: Flow[ProducerMessage.Message[Array[Byte], String, None.type], ProducerMessage.Result[Array[Byte], String, None.type], NotUsed] =
    RestartFlow.withBackoff(1.seconds, 2.seconds, 0.5) {
      () => kafka.flow()
    }

  // log to the console
  val consoleSink: Sink[Any, NotUsed] = RestartSink.withBackoff(1.seconds, 2.seconds, 0.5) {
    () => Sink.foreach(event => Logger.info(s"[After Kafka] Docker Swarm event: $event"))
  }

  // define the stream graph
  val stream: RunnableGraph[NotUsed] = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    swarmSource ~> logger ~> toKafkaRecord ~> toKafkaMessage ~> kafkaFlow ~> consoleSink

    ClosedShape
  })

  // finally, materialize and run
  stream.run()(app.materializer)
}