package com.hatri.core

import com.typesafe.config.ConfigFactory

object HatriConfig {
  val config = ConfigFactory.load()

  val twitterConsumerKey         = config.getString("twitter.consumer.key")
  val twitterConsumerKeySecret   = config.getString("twitter.consumer.secret")

  val twitterAccessKey           = config.getString("twitter.access.key")
  val twitterAccessKeySecret     = config.getString("twitter.access.secret")


  val kafkaHost             = config.getString("kafka.host")
  val kafkaPort             = config.getInt("kafka.port")
  val kafkaTopic            = config.getString("kafka.topic")

}