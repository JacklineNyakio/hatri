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

import com.hatri.core.HatriConfig.kafkaTopic

object HatriKafkaConsumer extends LazyLogging  with App{

  logger.info(
      "Polling for events from the accidents_data topic."
  )

  val topicName = kafkaTopic

  while(true) {
    val consumerOne = new AccidentsInfoConsumer() {
      override val logger = Logger("kafka consumer one")
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

    //polling the server for more data records.
    val firstConsumerRecords =
      consumerOne
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

    StdIn.readLine()
    consumerOne.consumer.close()
  }

}

trait AccidentsInfoConsumer {
  val logger :  Logger

//  configuring the kafka consumer.
  val props : Properties = new Properties()

  props.put(
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
  )
  props.put(
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
    classOf[StringDeserializer]
  )
  props.put(
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
    classOf[StringDeserializer]
  )

  props.put(
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
    "earliest"
  )

  props.put(
    ConsumerConfig.CLIENT_ID_CONFIG,
    "accidents-kenya"
  )

  val consumer = new KafkaConsumer[String, String](props)

}

