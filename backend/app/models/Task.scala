package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class Task(service: String, status: String)

object Task {
  implicit final val reads: Reads[Task] = (
    (JsPath \ "ServiceID").read[String] and
      (JsPath \ "DesiredState").read[String]
    ) (Task.apply _)
}
