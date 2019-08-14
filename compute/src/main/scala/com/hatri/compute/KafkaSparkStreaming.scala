package com.hatri.compute

import org.apache.log4j.Logger

import org.apache.spark._

import sql.functions._
import sql.SparkSession
import sql.DataFrame

import com.hatri._
import core.HatriConfig.{kafkaHost, kafkaPort, kafkaTopic}

object KafkaSparkStreaming {

  // creating a spark session
  val spark = SparkSession
    .builder()
    .master("local[*]")
    .appName("kafka-spark-jobs")
    .getOrCreate()
  val log: Logger = Logger.getLogger("KAFKA SPARK STREAMING.")

  def main(args: Array[String]): Unit = {
    log.info("running a spark streaming job that reads from kafka and writes to scyllaDb")

    //  reading data from kafka by subscribing to a a topic
    val kafkaStream = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", s"$kafkaHost:$kafkaPort")
      .option("subscribe", kafkaTopic)
      .option("startingOffsets","earliest")
      .option("kafkaConsumer.pollTimeoutMs", "1000")
      .load()

    val query1 = kafkaStream
      .writeStream
      .outputMode("append")
      .format("console")
      .start()

    log.info("kafka stream schema :")
    kafkaStream.printSchema()

    query1.awaitTermination()

  }
}