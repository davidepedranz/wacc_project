package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

final case class ScaleService(name: String, image: String, replicas: Int)

object ScaleService {
  implicit final val reads: Reads[ScaleService] = (
    (JsPath \ "Name").read[String] and
      (JsPath \ "TaskTemplate" \ "ContainerSpec" \ "Image").read[String] and
      (JsPath \ "Mode" \ "Replicated" \ "Replicas").readNullable[Int].map(x => x.getOrElse(1))
    ) (ScaleService.apply _)

  implicit final val writes: OWrites[ScaleService] = Json.writes[ScaleService]
}
