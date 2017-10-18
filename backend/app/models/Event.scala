package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

import scala.language.postfixOps

case class Event(time: Long, action: String, service: String, host: String)

object Event {
  implicit val reads: Reads[Event] = (
    (JsPath \ "time").read[Long] and
      (JsPath \ "Action").read[String] and
      (JsPath \ "Actor" \ "Attributes" \ "name").read[String] and
      (JsPath \ "Actor" \ "ID").read[String]
    ) (Event.apply _)
  implicit val writes: OWrites[Event] = Json.writes[Event]
}
