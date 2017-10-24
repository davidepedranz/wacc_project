package services

import akka.actor.Scheduler
import akka.pattern.after

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}

/**
  * Utility to retry failed Futures later.
  */
object Retry {

  /**
    * Execute the given future until it succeeds.
    *
    * @param future    Future to execute.
    * @param delay     Amount of time to wait between successive retries.
    * @param callback  Callback function to call before each retry (pass null if this feature is not needed).
    * @param ec        Implicit execution context.
    * @param scheduler Akka Scheduler, used to postpone the retries of the failed future.
    * @tparam T Type of the future.
    * @return A future that will be recovered forever, until it succeeds.
    */
  def periodically[T](future: => Future[T], delay: FiniteDuration, callback: (FiniteDuration, Throwable) => Any)
                     (implicit ec: ExecutionContext, scheduler: Scheduler): Future[T] = {
    future.recoverWith {
      case ex =>
        if (callback != null) {
          callback(delay, ex)
        }
        after(delay, scheduler)(periodically(future, delay, callback))
    }
  }
}
