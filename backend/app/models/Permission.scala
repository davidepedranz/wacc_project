package models

import be.objectify.deadbolt.scala.models.{Permission => DeadboldPermission}

case class Permission(value: String) extends DeadboldPermission

object Permission {

  // users
  val USERS_READ = "users.read"
  val USERS_WRITE = "users.write"

  // services
  val SERVICES = "services"

  // events
  val EVENTS = "events"

  // all events (used for the validation)
  val ALL = Set(USERS_READ, USERS_WRITE, SERVICES, EVENTS)

  /**
    * Check if the string is a valid permission.
    */
  def validate(permission: String): Boolean = {
    ALL.contains(permission)
  }
}
