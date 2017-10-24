package repositories

import java.util.Date

import com.outworkers.phantom.ResultSet
import models.{Event}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}

final class EventRepositorySpec extends PlaySpec with GuiceOneAppPerTest with BeforeAndAfterEach {
  private val injector: Injector = new GuiceApplicationBuilder().build.injector
  private val repository: EventsRepository = injector.instanceOf[EventsRepository]

  private val MAX_DURATION: FiniteDuration = 2 seconds

  private val key1 = new Date(System.currentTimeMillis())
  private val key2 = new Date(System.currentTimeMillis() - 24*3600*1000)
  private val TEST_EVENT_1 = new Event(key1, System.currentTimeMillis(), "test-action1", "test-service1", "test-host1")
  private val TEST_EVENT_2 = new Event(key2, System.currentTimeMillis(), "test-action2", "test-service2", "test-host2")

  private def saveEvent(event: Event): Unit = {
      val result: ResultSet = Await.result(repository.saveOrUpdate(event), MAX_DURATION)
      result.wasApplied() mustBe true
  }

  private def assertExists(date: Date) = {
    val result: List[Event] = Await.result(repository.read(date), MAX_DURATION)
    result must not be empty
    result.length must be > 0
  }

  private def assertNotExists(date: Date) = {
    val result: List[Event] = Await.result(repository.read(date), MAX_DURATION)
    result mustBe empty
  }

  override protected def beforeEach(): Unit = {
    repository.start()
    repository.cleanup()
    System.out.println("create table if not yet created")
  }

  override protected def afterEach(): Unit = {
    repository.cleanup()
    System.out.println("truncate all data")
  }

  "EventsDatabase" when {
    "#saveOrUpdate" should {
      "save the even in the repository" in {
        saveEvent(TEST_EVENT_1)
        assertExists(TEST_EVENT_1.date)
      }
    }
  }

  "#read" should {
    "return an empty list if the repository is empty" in {
      val result: List[Event] = Await.result(repository.read(key1), MAX_DURATION)
      result mustBe empty
    }
    "return a list with an event after saving one event" in {
      saveEvent(TEST_EVENT_2)
      val result: List[Event] = Await.result(repository.read(key2), MAX_DURATION)
      result(0).time mustEqual TEST_EVENT_2.time
      result(0).action mustEqual TEST_EVENT_2.action
      result(0).service mustEqual TEST_EVENT_2.service
      result(0).host mustEqual TEST_EVENT_2.host
    }
  }
}
