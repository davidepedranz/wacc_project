package models

import com.outworkers.phantom.builder.primitives.Primitive
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._


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
}
