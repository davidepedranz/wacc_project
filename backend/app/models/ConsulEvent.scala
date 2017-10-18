package models

import play.api.libs.json._
import org.joda.time.DateTime
import play.api.libs.json.JodaWrites
import play.api.libs.json.JodaReads


/**
  *
  * This is the Scala representation of Songs, following the Datastax example
  */
case class ConsulEvent(
                 id: String,
                 events: ConsulEventItem,
                 datetime: DateTime
               )



object ConsulEvent{
  val dateFormat = "yyyy-MM-dd HH:mm:ss"

  implicit val dateTimeWriter: Writes[DateTime] = JodaWrites.jodaDateWrites(dateFormat)
  implicit val dateTimeJsReader = JodaReads.jodaDateReads("yyyyMMddHHmmss")
  implicit val consulEventReads  = Json.format[ConsulEvent]
}


