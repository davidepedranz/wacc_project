package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

case class Credentials(username: String, password: String)

object Credentials {
  implicit val reads: Reads[Credentials] = (
    (JsPath \ "username").read[String] and
      (JsPath \ "password").read[String]
    ) (Credentials.apply _)
}
