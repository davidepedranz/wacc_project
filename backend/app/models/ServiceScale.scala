package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class ServiceScale( name: String, image: String, replicas : Int)

object ServiceScale {
  implicit val reads: Reads[ServiceScale] = (
    (JsPath \ "Name").read[String] and
    (JsPath \ "TaskTemplate" \ "ContainerSpec" \ "Image").read[String] and
    (JsPath  \ "Mode" \ "Replicated" \ "Replicas").readNullable[Int].map(x => x.getOrElse(1))
    ) (ServiceScale.apply _)

  implicit val writes: OWrites[ServiceScale] = Json.writes[ServiceScale]
}
