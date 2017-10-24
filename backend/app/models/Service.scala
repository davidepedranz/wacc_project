package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Service(id: String, name: String, mode: String, current: Int, wanted: Int, image: String)

object Service {
  implicit val reads: Reads[Service] = (
    (JsPath \ "ID").read[String] and
      (JsPath \ "Spec" \ "Name").read[String] and
      (JsPath \ "Spec" \ "Mode").read[JsObject].map(o => o.keys.headOption.getOrElse("")) and
      (JsPath \ "Version" \ "Index").readNullable[Int].map(_ => 0) and
      (JsPath \\ "Replicas").readNullable[Int].map(x => x.getOrElse(-1)) and
      (JsPath \ "Spec" \ "TaskTemplate" \ "ContainerSpec" \ "Image").read[String].map(raw => raw.split("@").head)
    ) (Service.apply _)

  implicit val writes: OWrites[Service] = Json.writes[Service]
}
