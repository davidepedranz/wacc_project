package authorization

import javax.inject.{Inject, Singleton}

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}

/**
  * Class that implements the cache logic for the authorizations.
  * This is required by the Deadbolt library. Our strategy is not to cache anything.
  * All verifications for the permissions are done against the users' repository.
  *
  * @param defaultHandler Default Deadbolt cache handler.
  */
@Singleton
final class AuthorizationCache @Inject()(defaultHandler: DeadboltHandler) extends HandlerCache {

  // get the default handler
  override def apply(): DeadboltHandler = defaultHandler

  // no handler... we do not cache the authorizations
  override def apply(handlerKey: HandlerKey): DeadboltHandler = Map.empty(handlerKey)
}
