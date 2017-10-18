package controllers

import java.util.Date
import javax.inject._

import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltHandler}
import models.{ConsulEvent, ConsulEventItem, Event}
import org.joda.time.DateTime
import play.api.Logger
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.Json
import play.api.mvc._
import repositories.eventDatabase

import scala.concurrent.ExecutionContext

@Singleton
final class CassandraController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents, bodyParsers: PlayBodyParsers,
                                          handler: DeadboltHandler, actionBuilders: ActionBuilders) extends AbstractController(cc) {

  def list(dateStr: String) = Action.async { implicit req =>
    val date = new Date(DateTime.parse(dateStr, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).getMillis)
    Logger.debug("Called reading: " + date)
    eventDatabase.start()

    // read data
    for {
      ans <- eventDatabase.read(date)
    } yield {
      Ok(Json.toJson(ans))
    }
  }

  def test = Action.async { implicit req =>
    Logger.debug("Called")
    eventDatabase.start()
    val dt = DateTime.parse("2017-10-16 19:13:37", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    val key = new Date(System.currentTimeMillis())

    // save testing data
    val r = scala.util.Random
    val evt = new Event(key, System.currentTimeMillis(), r.nextInt().toString, r.nextInt().toString, r.nextInt().toString)
    val evt1 = new Event(key, System.currentTimeMillis(), r.nextInt().toString, r.nextInt().toString, r.nextInt().toString)
//    consulEventDatabase.saveOrUpdate(evt)
//    consulEventDatabase.saveOrUpdate(evt1)

    // read data
    for {
      ans <-  eventDatabase.saveOrUpdate(evt)
      ans1 <-  eventDatabase.saveOrUpdate(evt1)
    } yield {
      Ok(ans.toString + ans1.toString)
    }

////    Deleting
//    consulEventDatabase.deleteByDatetime("cassandra-test", dt)

  }


}