package com.besquare
package shifts
package schedule

import scala.util._

import akka.actor._
import akka.pattern._

import spray.json._
import spray.httpx.SprayJsonSupport._
import spray.routing._
import spray.http.StatusCodes.{ Success ⇒ _, _ }

import common._
import calendar._
import resource._

trait ScheduleApiRoutesProvider extends Directives {
  def scheduleApiRoutes(calendar: ActorRef): RequestContext ⇒ Unit
}

trait ScheduleApiRoutes extends ScheduleApiRoutesProvider
    with AskTimeoutProvider
    with ActorRefFactoryExecutionContextProvider
    with ScheduleMarshalling {

  import Calendar._
  import ScheduleActor._
  import SprayIdSupport._

  def rawJson = extract { _.request.entity.asString }

  def scheduleApiRoutes(calendar: ActorRef) = {
    // format: OFF
    pathPrefix("schedules") {
      pathPrefix(Segment) { 
        getSchedule(calendar) { schedule => 
          path("solve") { get {
            onComplete(schedule ? GetModelData) {
              case Success(str: String) => complete(str)
              case Failure(ex) => complete(InternalServerError)
            }
          }} ~
          path("constraints") {
            (put & entity(as[ResourceConstraints])) { constraints =>
              schedule ! UpdateResourceConstraints(constraints)
              complete(NoContent)
            }
          } ~
          pathPrefix(Segment) { unpackUuid { taskId =>
            pathEnd {
              delete {
                onComplete(schedule ? RemoveAssignment(taskId)) {
                  case Success(AssignmentRemoved(_)) => complete(NoContent)
                  case Failure(ex) => complete(InternalServerError, ex)
                }
              }
            } ~
            path(Segment) { unpackUuid { resourceId =>
              pathEnd {
                post {
                  onComplete(schedule ? AddAssignment(taskId, resourceId)) {
                    case Success(AssignmentAdded(_)) => complete(Created)
                    case Failure(ex) => complete(InternalServerError, ex)
                  }
                }
              }
            }}
          }} ~ pathEnd {
            get {
              onComplete(schedule ? GetState) {
                case Success(GotState(schedule)) => complete(schedule)
                case Success(ScheduleWasNotFound) => complete(NotFound)
                case Failure(ex) => complete(InternalServerError, ex)
              }
            }
          }
        }
      } ~
      pathEnd {
        get {
          onComplete(calendar ? GetSchedules) {
            case Success(GotSchedules(schedules)) => complete(schedules)
            case Success(_) => complete("An unexpected success happened")
            case Failure(ex) => complete(InternalServerError, ex)
          }
        }
      }
    }
    // format: ON
  }

  def getSchedule(calendar: ActorRef)(handler: ActorRef ⇒ (RequestContext ⇒ Unit))(scheduleName: String)(implicit actorRefFactory: ActorRefFactory): (RequestContext ⇒ Unit) = {
    def scheduleSpecificRoutes = (calendar ? GetSchedule(scheduleName)).map {
      case GotSchedule(schedule) ⇒ handler(schedule)
      case ScheduleWasNotFound   ⇒ complete(NotFound, "The schedule does not exist")
    }

    onComplete(scheduleSpecificRoutes) {
      case Success(route) ⇒ route
      case Failure(ex)    ⇒ complete(InternalServerError, ex)
    }
  }
}

object SprayIdSupport {
  def unpackUuid(handler: Id ⇒ (RequestContext ⇒ Unit))(idString: String): (RequestContext ⇒ Unit) = {
    val uuid = Id(idString)
    if (uuid.isValidUUID)
      handler(uuid)
    else
      directives.RouteDirectives.reject(MalformedRequestContentRejection(idString))
  }
}
