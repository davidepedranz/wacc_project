package models

import be.objectify.deadbolt.scala.models.{Permission => DeadboldPermission}

case class Permission(value: String) extends DeadboldPermission

object Permission {
  val USERS_READ = "users.read"
  val USERS_WRITE = "users.write"
}
