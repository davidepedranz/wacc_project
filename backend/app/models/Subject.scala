package models

import be.objectify.deadbolt.scala.models.{Role, Subject => DeadboldSubject}

case class Subject(identifier: String, permissions: List[Permission]) extends DeadboldSubject {
  override def roles: List[Role] = List.empty
}

object Subject {
  implicit def fromUser(user: User): Subject = Subject(
    identifier = user.username,
    permissions = user.permissions.map(value => Permission(value)).toList
  )
}