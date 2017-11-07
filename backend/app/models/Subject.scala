package models

import be.objectify.deadbolt.scala.models.{Role, Subject => DeadboldSubject}

import scala.language.implicitConversions

final case class Subject(identifier: String, permissions: List[Permission]) extends DeadboldSubject {
  override def roles: List[Role] = List.empty
}

object Subject {
  implicit final def fromUser(user: User): Subject = Subject(
    identifier = user.username,
    permissions = user.permissions.map(value => Permission(value)).toList
  )

  implicit final def toUser(subject: DeadboldSubject): User = User(
    username = subject.identifier,
    permissions = subject.permissions.map(permission => permission.value).toSet
  )
}