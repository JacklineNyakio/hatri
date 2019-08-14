package com.hatri.ingestion

import java.util.Properties

import org.apache.kafka._
import clients.producer.{ KafkaProducer, ProducerConfig, ProducerRecord, Callback, RecordMetadata }
import common.serialization.StringSerializer

import com.typesafe.scalalogging.LazyLogging

import com.hatri.core.HatriConfig._

object HatriKafkaProducer extends LazyLogging {

  //  creating a producer object that has values to pass to the producer.
  lazy val props = new Properties()
  //  List of host:port pairs the producer will use to establish connections, give at least two in case one breaks.
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
    s"$kafkaHost:$kafkaPort")
  //  class of be used to serialize keys of records.(key objects to byte array)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    classOf[StringSerializer])
  //  class of be used to serialize value of records.(K:V expected as byte array)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    classOf[StringSerializer])
  //  An id string to pass to the server when making requests
  props.put(ProducerConfig.CLIENT_ID_CONFIG,
    kafkaTopic)

  def writeToKafka(key: String, value: String ) : Unit = {
    //  instantiate a producer.
    val producer = new KafkaProducer[String, String](props)

    val producerRecord = new ProducerRecord[String, String](
      kafkaTopic,
      key,
      value
    )
//    A callback interface that the user can implement to allow code to execute when the request is complete.
    val callback = new Callback {
      override def onCompletion(
         metadata: RecordMetadata,
         exception: Exception
      ): Unit = {
        if(exception == null)
          logger.info("Successful write")
        else
          logger.warn(s"Failed write: $exception")
      }
    }
    producer.send(producerRecord, callback)

  }


}









