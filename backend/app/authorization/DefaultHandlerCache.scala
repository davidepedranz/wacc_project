package authorization

import javax.inject.{Inject, Singleton}

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}

@Singleton
class DefaultHandlerCache @Inject()(defaultHandler: DeadboltHandler) extends HandlerCache {

  // TODO: is this correct?
  val handlers: Map[Any, DeadboltHandler] = Map.empty

  // Get the default handler.
  override def apply(): DeadboltHandler = defaultHandler

  // Get a named handler
  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}
