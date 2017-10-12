package models

import play.api.libs.json.{Json, OWrites}

case class Event(id: String, timestamp: Long)

object Event {
  implicit val writes: OWrites[Event] = Json.writes[Event]
}
