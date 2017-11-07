package models

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{JsPath, Reads}

final case class Credentials(username: String, password: String)

object Credentials {
  implicit final val reads: Reads[Credentials] = (
    (JsPath \ "username").read[String] and
      (JsPath \ "password").read[String]
    ) (Credentials.apply _)
}
