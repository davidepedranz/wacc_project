package models

import be.objectify.deadbolt.scala.models.{Permission => DeadboldPermission}

final case class Permission(value: String) extends DeadboldPermission

object Permission {

  // users
  final val USERS_READ = "users.read"
  final val USERS_WRITE = "users.write"

  // services
  final val SERVICES = "services"

  // events
  final val EVENTS = "events"

  // all events (used for the validation)
  final val ALL = Set(USERS_READ, USERS_WRITE, SERVICES, EVENTS)

  /**
    * Check if the string is a valid permission.
    */
  def validate(permission: String): Boolean = {
    ALL.contains(permission)
  }
}
