package com.hatri.ingestion

import spray.json._

import DefaultJsonProtocol._

case class Accidents (
    accidentType : String,
    location: String,
    casualties : String,
    cause:Option[String]
)

object KafkaJsonConversion extends DefaultJsonProtocol {
  implicit val accidentsFormat = jsonFormat4(Accidents)
}

