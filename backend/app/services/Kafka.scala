package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Sink, Source}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import play.api.Configuration

@Singleton
final class Kafka @Inject()(configuration: Configuration) {
  private val actorSystem: ActorSystem = ActorSystem("kafka")
  private val kafkaUrl: String = configuration.get[String]("kafka.url")
  private val kafkaGroup: String = configuration.get[String]("kafka.group")

  private def producerSettings: ProducerSettings[Array[Byte], String] = {
    ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(kafkaUrl)
  }

  private def consumerSettings: ConsumerSettings[Array[Byte], String] = {
    ConsumerSettings(actorSystem, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(kafkaUrl)
      .withGroupId(kafkaGroup)
  }

  def sink: Sink[ProducerRecord[Array[Byte], String], _] = {
    Producer.plainSink(producerSettings)
  }

  def source(topic: String): Source[ConsumerRecord[Array[Byte], String], _] = {
    Consumer.plainSource(consumerSettings, Subscriptions.topics(topic))
  }
}
