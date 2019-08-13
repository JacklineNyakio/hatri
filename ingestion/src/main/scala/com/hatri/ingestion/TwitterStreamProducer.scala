package com.hatri.ingestion

import java.util.concurrent.{ BlockingQueue, LinkedBlockingQueue }

import com.google.common.collect.Lists

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.{ Client, Constants }
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.auth.{ Authentication, OAuth1 }

import com.typesafe.scalalogging.LazyLogging

import com.hatri._

import core.HatriConfig._

object TwitterStreamProducer extends LazyLogging with HatriKafkaProducerT with App  {

  val queue : BlockingQueue[String] = new LinkedBlockingQueue[String](10000)
  val endpoint: StatusesFilterEndpoint = new StatusesFilterEndpoint()
  endpoint.trackTerms(Lists.newArrayList("@ma3routes", "nairobi","ajali","highway","accident","jam","road accident","traffic","kenya","@KenyanTraffic","RedCross"))

  val auth : Authentication = new OAuth1(twitterConsumerKey, twitterConsumerKeySecret, twitterAccessKey, twitterAccessKeySecret)

  val client : Client = new ClientBuilder()
    .hosts(Constants.STREAM_HOST)
    .endpoint(endpoint)
    .authentication(auth)
    .processor(new StringDelimitedProcessor(queue))
    .build()

  client.connect()

  for (msgRead <- 0 until 10000) {
    writeToKafka(
      kafkaTopic,
      queue.take()
    )
  }

client.stop()
}
