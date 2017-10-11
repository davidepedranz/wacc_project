package models

import play.api.libs.json.{Json, OWrites}

case class Token(token: String)

object Token {
  implicit val writes: OWrites[Token] = Json.writes[Token]
}
