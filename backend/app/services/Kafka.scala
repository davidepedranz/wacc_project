package services

import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.{Done, NotUsed}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import play.api.{Configuration, Logger}

import scala.concurrent.Future

@Singleton
final class Kafka @Inject()(configuration: Configuration) {
  private val actorSystem: ActorSystem = ActorSystem("kafka")
  private val kafkaUrl: String = configuration.get[String]("kafka.url")

  private def producerSettings: ProducerSettings[Array[Byte], String] = {
    ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(kafkaUrl)
  }

  private def consumerSettings(group: String): ConsumerSettings[Array[Byte], String] = {
    ConsumerSettings(actorSystem, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(kafkaUrl)
      .withGroupId(group)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  }

  def flow(): Flow[ProducerMessage.Message[Array[Byte], String, None.type], ProducerMessage.Result[Array[Byte], String, None.type], NotUsed] = {
    Logger.info(s"Kafka -> requested new flow.")
    Producer.flow(producerSettings)
  }

  def sink(): Sink[ProducerRecord[Array[Byte], String], Future[Done]] = {
    Logger.info(s"Kafka -> requested new sink.")
    Producer.plainSink(producerSettings)
  }

  def source(topic: String, group: String = UUID.randomUUID().toString): Source[ConsumerRecord[Array[Byte], String], Control] = {
    Logger.info(s"Kafka -> requested new source: topic=$topic, group=$group")
    Consumer.plainSource(consumerSettings(group), Subscriptions.topics(topic))
  }
}
