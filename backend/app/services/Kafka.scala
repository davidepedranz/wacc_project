package services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, Source}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import play.api.Configuration

import scala.concurrent.Future

@Singleton
final class Kafka @Inject()(configuration: Configuration) {
  private val actorSystem: ActorSystem = ActorSystem("kafka")
  private val kafkaUrl: String = configuration.get[String]("kafka.url")

  private def producerSettings: ProducerSettings[Array[Byte], String] = {
    ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(kafkaUrl)
  }

  private def consumerSettings: ConsumerSettings[Array[Byte], String] = {
    ConsumerSettings(actorSystem, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(kafkaUrl)
      .withGroupId(UUID.randomUUID().toString)
  }

  def sink(): Sink[ProducerRecord[Array[Byte], String], Future[Done]] = {
    Producer.plainSink(producerSettings)
  }

  def source(topic: String): Source[ConsumerRecord[Array[Byte], String], Control] = {
    Consumer.plainSource(consumerSettings, Subscriptions.topics(topic))
  }
}
