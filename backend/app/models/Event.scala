package models

import java.util.Date

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

import scala.language.postfixOps

final case class Event(date: Date, time: Long, action: String, service: String, host: String)

object Event {
  implicit final val reads: Reads[Event] = (
    (JsPath \ "time").read[Long].map(timeSeconds => new Date(timeSeconds * 1000)) and
      (JsPath \ "time").read[Long] and
      (JsPath \ "Action").read[String] and
      (JsPath \ "Actor" \ "Attributes" \ "name").read[String] and
      (JsPath \ "Actor" \ "ID").read[String]
    ) (Event.apply _)
  implicit final val writes: OWrites[Event] = Json.writes[Event]
}
