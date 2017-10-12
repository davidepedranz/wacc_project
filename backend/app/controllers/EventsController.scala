package controllers

import javax.inject._

import akka.stream.javadsl.Source
import akka.stream.scaladsl.{Flow, Sink}
import models.Event
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class EventsController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents) extends AbstractController(cc) {

  private implicit val messageFlowTransformer: MessageFlowTransformer[String, Event] = {
    MessageFlowTransformer.jsonMessageFlowTransformer[String, Event]
  }

  def socket: WebSocket = WebSocket.acceptOrResult[String, Event] { request =>
    // TODO: parse token + authenticate... return Left if token / permission wrong

    Future {

      // ignore input from the client
      val in = Sink.ignore

      // send a "Hello", then tick every 1 seconds (+ keep connection open)
      val out = Source.single(Event("Hello!", System.currentTimeMillis()))
        .concat(Source.tick(1 seconds, 2 seconds, Event("tick", System.currentTimeMillis())))
        .concat(Source.maybe)

      // client -> in -> [...ignored] -> events -> out -> client
      Right(Flow.fromSinkAndSource(in, out))
    }
  }

  //  def socket: WebSocket = WebSocket.accept[String, Event] { request =>
  //
  //    // ignore input from the client
  //    val in = Sink.ignore
  //
  //    // send a "Hello", then tick every 1 seconds (+ keep connection open)
  //    val out = Source.single(Event("Hello!", System.currentTimeMillis()))
  //      .concat(Source.tick(1 seconds, 2 seconds, Event("tick", System.currentTimeMillis())))
  //      .concat(Source.maybe)
  //
  //    // client -> in -> [...ignored] -> events -> out -> client
  //    Flow.fromSinkAndSource(in, out)
  //  }

}
