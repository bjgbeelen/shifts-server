package com.besquare
package shifts
package calendar

import scala.util._
import scala.concurrent._
import scala.concurrent.duration._

import akka.actor._
import akka.pattern._

import spray.http.StatusCodes.{ Success ⇒ _, _ }
import spray.json._
import spray.httpx.SprayJsonSupport._
import spray.routing._

import task._
import resource._
import schedule._
import common._
import marshalling._
import counter._

trait CalendarApiRoutes extends Directives
    with TaskApiRoutesProvider
    with ResourceApiRoutesProvider
    with ScheduleApiRoutesProvider
    with CounterApiRoutesProvider
    with CalendarViewMarshalling
    with CalendarMarshalling
    with CalendarsActorProvider
    with AskTimeoutProvider
    with ActorRefFactoryExecutionContextProvider {

  import Calendar._
  import CalendarsActor._

  def calendarApiRoutes = {
    // format: OFF
    pathPrefix("calendars") {
      pathEnd {
        get {
          onSuccess(calendarsActor ? GetAvailableCalendars) {
            case GotAvailableCalendars(list) => complete(list)
          }          
        } ~ 
        (post & entity(as[CreateCalendar])) { request =>
           onSuccess(calendarsActor ? request) {
            case CalendarCreated(_) => complete(Created)
            case CalendarDidAlreadyExist => complete(BadRequest, "The calendar with this name already exists")
           }
        }
      } ~
      pathPrefix(Segment) {
        getCalendar { calendar => 
          pathEnd {
            get {
              onSuccess(calendar ? GetView) {
                case GotView(view) ⇒ complete(view)
              }
            }
          } ~ taskApiRoutes(calendar) ~ resourceApiRoutes(calendar) ~ scheduleApiRoutes(calendar) ~ counterApiRoutes(calendar)
        }   
      }
    }
    // format: ON
  }

  def getCalendar(handler: ActorRef ⇒ (RequestContext ⇒ Unit))(calendarName: String)(implicit actorRefFactory: ActorRefFactory): (RequestContext ⇒ Unit) = {
    def calendarSpecificRoutes = (calendarsActor ? GetCalendar(calendarName)).map {
      case GotCalendar(calendar) ⇒ handler(calendar)
      case CalendarWasNotFound   ⇒ complete(NotFound, "The calendar does not exist")
    }

    onComplete(calendarSpecificRoutes) {
      case Success(route) ⇒ route
      case Failure(ex)    ⇒ complete(InternalServerError, ex)
    }
  }
}
