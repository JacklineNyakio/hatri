package com.hatri.ingestion

import java.time.Duration
import java.util.Properties

import scala.collection.JavaConversions._
import scala.io.StdIn

import com.typesafe.scalalogging.{LazyLogging, Logger}
import org.apache.kafka._
import clients.consumer.{ConsumerConfig, KafkaConsumer}
import common.TopicPartition
import common.serialization.StringDeserializer

object HatriKafkaConsumer extends App with LazyLogging {

  logger.info(
      "Polling for events from the accidents-kenya topic."
  )

  val topicName = "accidents-kenya"

  while(true) {
    val consumerOne = new AccidentsInfoConsumer() {
      override val logger = Logger("kafka consumer 1")
    }

    val consumerTwo = new AccidentsInfoConsumer() {
      override val logger = Logger("kafka consumer 2")
    }

    //  subscribing to a topic (Manually) see assign vs subscribe
    consumerOne.consumer.assign(
      List(
        new TopicPartition(
          topicName,
          0
        )
      )
    )

    consumerTwo.consumer.assign(
      List(
        new TopicPartition(
          topicName,
          1
        )
      )
    )

    val firstConsumerRecords =
      consumerOne
      .consumer
      .poll(Duration.ofMillis(1000))

    val secondConsumerRecords =
      consumerTwo
        .consumer
        .poll(Duration.ofMillis(1000))

    // check if the consumers are getting the messages.
    if(firstConsumerRecords.count() != 0) {
      firstConsumerRecords.foreach{ record =>
        consumerOne.logger.info(
          s"Consumed tweet : ${record.value()} in partition: ${record.partition()}"
        )
      }
    }

    if(secondConsumerRecords.count() != 0) {
      secondConsumerRecords.foreach{ record =>
        consumerTwo.logger.info(
          s"Consumed tweet : ${record.value()} in partition: ${record.partition()}"
        )
      }
    }

    StdIn.readLine()
    consumerOne.consumer.close()
    consumerTwo.consumer.close()
  }

}

trait AccidentsInfoConsumer {
  val logger :  Logger

//  configuring the kafka consumer.
  val props : Properties = new Properties()

  props.put(
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092"
  )
  props.put(
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
    classOf[StringDeserializer]
  )
  props.put(
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    classOf[StringDeserializer]
  )

  val consumer = new KafkaConsumer[String, String](props)

}

