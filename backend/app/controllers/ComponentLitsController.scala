
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

/**/

/**/

@Singleton
class ComponentListController @Inject()(ws : WSClient) extends Controller {
  
  val host = "http://localhost:2375" //sys.env.get("DOCKER_HOST").mkString
  val getContainersUrl = "/containers/json"
  val getContainersAllUrl = "/containers/json?all=true"
  val getImagesUrl = "/images/json"
  
  def getContainers = Action{ 
  val res = scala.io.Source.fromURL(host+getContainersUrl).mkString;
  Ok(res)
  }

  def getContainersAll = Action{
    request => Ok(scala.io.Source.fromURL(host + getContainersAllUrl).mkString)
  }

  def getContainersInfo(id : String ) = Action {
    request => Ok (scala.io.Source.fromURL(host+"/containers/"+id+"/json").mkString )
  }

  def getImages = Action {
    request => 
    Ok( scala.io.Source.fromURL(host + getImagesUrl).mkString )
  } 


  def postContainerStop(id: String) = Action {    
    val containerStop = "curl  -X POST "+host+"/containers/"+id+"/stop" !!;
    //Ok(containerStop)
    redirect("/")
    }

  
  def postContainerStart(id : String ) = Action {
  val containerStart = "curl -X POST "+host+"/containers/"+id+"/start" !!;
  //Ok(containerStart)
  redirect("/")
  }

}
