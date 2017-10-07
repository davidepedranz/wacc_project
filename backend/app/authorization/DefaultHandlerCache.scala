package authorization

import javax.inject.Singleton

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}

@Singleton
class DefaultHandlerCache extends HandlerCache {
  val defaultHandler: DeadboltHandler = new DefaultDeadboltHandler

  // TODO: is this correct?
  val handlers: Map[Any, DeadboltHandler] = Map.empty

  // Get the default handler.
  override def apply(): DeadboltHandler = defaultHandler

  // Get a named handler
  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}
