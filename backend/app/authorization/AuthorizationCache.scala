package authorization

import javax.inject.{Inject, Singleton}

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}

@Singleton
class AuthorizationCache @Inject()(defaultHandler: DeadboltHandler) extends HandlerCache {

  // no handler... we do not cache the authorizations
  private val handlers: Map[Any, DeadboltHandler] = Map.empty

  // get the default handler
  override def apply(): DeadboltHandler = defaultHandler

  // get the default handler
  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}
