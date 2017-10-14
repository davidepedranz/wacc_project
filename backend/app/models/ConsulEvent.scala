package models
import java.util.UUID

import org.joda.time.DateTime


/**
  *
  * This is the Scala representation of Songs, following the Datastax example
  */
case class ConsulEvent(
                 id: UUID,
                 events: ConsulEventItem,
                 datetime: DateTime
               )
