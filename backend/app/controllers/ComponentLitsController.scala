
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

import sys.process._


class Application @Inject()(ws: WSClient) extends Controller {
  val url = "http:/v1.29/containers/json"
  val response: WSRequest = ws.url(url) 

}

/**/


@Singleton
class ComponentListController @Inject()(ws : WSClient) extends Controller {
  
  //val containersGetResult = "curl --unix-socket /var/run/docker.sock http:/containers/json?all=true" !!
  //val imagesGet = "curl --unix-socket /var/run/docker.sock http:/images/json" !! 
  val host = "http://localhost:2375"
  val getContainersUrl = "/containers/json"
  val getContainersAllUrl = "/containers/json?all=true"
  val getImagesUrl = "/images/json"

  def getContainers = Action{ 
  val res = scala.io.Source.fromURL(host+getContainersUrl).mkString;
  Ok(res)
    //the response of this:curl --unix-socket /var/run/docker.sock http:/v1.24/containers/json
  }

  def getContainersAll = Action{
    request => Ok(scala.io.Source.fromURL(host + getContainersAllUrl).mkString)
  }

  def getImages = Action {
    request => 
    Ok(scala.io.Source.fromURL(host + getImagesUrl).mkString)
  } 


  def postContainerStop(id: String) = Action {
    //request =>
    
    val containerStop = "curl  -X POST "+host+"/containers/"+id+"/stop" !!;
    Ok(containerStop)
    }

  
  def postContainerStart(id : String ) = Action {
    //request => 
    //Ok(host+"/containers"+id+"/start")
  val containerStart = "curl -X POST "+host+"/containers/"+id+"/start" !!;
  Ok(containerStart)
  }
   
}
