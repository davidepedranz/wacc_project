package models

import com.outworkers.phantom.builder.primitives.Primitive
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import play.api.libs.json.Json


/**
  *
  * This is the Scala representation of Songs, following the Datastax example
  */
case class ConsulEventItem(
            Node: String,
            CheckID: String,
            Name:String,
            Status: String,
            Notes: String,
            Output: String,
            ServiceID: String,
            ServiceName: String,
             )

object ConsulEventItem {
  implicit val jsonPrimitive: Primitive[ConsulEventItem] = {
    Primitive.json[ConsulEventItem](obj => obj.asJson.noSpaces)(str => decode[ConsulEventItem](str).fold(e => throw new Exception(e), identity))
  }
  implicit val consulEventItemReads  = Json.format[ConsulEventItem]
}
