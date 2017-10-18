package services

import akka.actor.Scheduler
import akka.pattern.after

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

object Retry {
  def periodically[T](f: => Future[T], delay: FiniteDuration, callback: (FiniteDuration, Throwable) => Any)
                     (implicit ec: ExecutionContext, s: Scheduler): Future[T] = {
    f recoverWith {
      case ex =>
        if (callback != null) {
          callback(delay, ex)
        }
        after(delay, s)(periodically(f, delay, callback))
    }
  }
}
