
package controllers

import javax.inject._
import play.api._
import play.api.mvc._



/**/
import scala.concurrent.Future
import scala.concurrent.duration._

import play.api.libs.ws._
import play.api.http.HttpEntity

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.ExecutionContext

import sys.process._


class Application @Inject()(ws: WSClient) extends Controller {
  val url = "http:/v1.29/containers/json"
  val response: WSRequest = ws.url(url) 

}

/**/


@Singleton
class ComponentListController @Inject()(ws : WSClient) extends Controller {
  //TODO option -a for see every container, not only the running ones
  val containersGetResult = "curl --unix-socket /var/run/docker.sock http:/containers/json?all=true" !!
  val imagesGet = "curl --unix-socket /var/run/docker.sock http:/images/json" !! 



  def getContainers = Action{
    request => 
      Ok(containersGetResult)
    //the response of this:curl --unix-socket /var/run/docker.sock http:/v1.24/containers/json
  }

  def getImages = Action {
    request => 
    Ok( imagesGet )
  }


  def postContainerStop(id: String) = Action {
    request => 
    val containerStop = "curl --unix-socket /var/run/docker.sock -X POST http:/v1.24/containers/"+id+"/stop" !!;
    Ok(containerStop)
    }

  
  def postContainerStart(id : String ) = Action {
    request => 
    val containerStart = "curl --unix-socket /var/run/docker.sock -X POST http:/v1.24/containers/"+id+"/start" !!;
     Ok(containerStart)
  }
   
}
