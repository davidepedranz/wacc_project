package controllers

import javax.inject._

import be.objectify.deadbolt.scala.models.PatternType
import be.objectify.deadbolt.scala.{ActionBuilders, DeadboltActions, DeadboltHandler}
import models.{Permission, ScaleService}
import play.api.libs.json._
import play.api.mvc._
import services.Swarm

import scala.concurrent.{ExecutionContext, Future}

/**
  * Controller that defines the actions for the Docker Swarm services.
  * The user is able to list, modify and delete services. Each call is authorized for the `services` permission.
  */
@Singleton
final class ServicesController @Inject()(implicit ec: ExecutionContext, cc: ControllerComponents,
                                         handler: DeadboltHandler, deadbolt: DeadboltActions, actionBuilders: ActionBuilders,
                                         bodyParsers: PlayBodyParsers, swarm: Swarm) extends AbstractController(cc) {

  // authorize calls to the Docker Swarm APIs
  private val servicesPermission: actionBuilders.PatternAction.PatternActionBuilder = {
    actionBuilders.PatternAction(value = Permission.SERVICES, patternType = PatternType.EQUALITY)
  }

  /**
    * Return the list of services running in the Docker Swarm cluster.
    */
  def getServices: Action[AnyContent] = servicesPermission.defaultHandler() {
    swarm.getServices.map {
      services => Ok(Json.toJson(services))
    }
  }

  /**
    * Create a new service in the Docker Swarm cluster.
    */
  def createService: Action[JsValue] = servicesPermission.apply(parse.json) { request =>
    request.body.validate[ScaleService] match {
      case JsError(error) => Future(BadRequest(JsError.toJson(error)))
      case _: JsSuccess[ScaleService] => swarm.createService(request.body).map {
        case true => Created
        case false => Conflict
      }
    }
  }

  /**
    * Update an existing service in the Docker Swarm cluster.
    */
  def updateService(id: String): Action[JsValue] = servicesPermission.apply(parse.json) { request =>
    request.body.validate[ScaleService] match {
      case JsError(error) => Future(BadRequest(JsError.toJson(error)))
      case _: JsSuccess[ScaleService] => swarm.updateService(id, request.body).map(_ => NoContent)
    }
  }

  /**
    * Delete a service from the Docker Swarm cluster.
    */
  def deleteService(id: String): Action[AnyContent] = servicesPermission.defaultHandler() {
    swarm.deleteService(id).map(_ => NoContent)
  }
}
