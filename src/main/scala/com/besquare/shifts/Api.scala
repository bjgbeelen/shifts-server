package com.besquare
package shifts

import akka.actor._
import akka.io.IO
import spray.can.Http
import spray.can.Http._
import spray.http.HttpHeaders._
import spray.routing._

import common._
import calendar._
import task._
import resource._
import schedule._
import user._
import counter._

object Api {
  val name = "ShiftsOptimizerApi"
}

class Api extends HttpServiceActor
    with ActorSettingsProvider
    with Services
    with Routes {

  IO(Http)(context.system) ! Http.Bind(listener = self, interface = settings.Http.host, port = settings.Http.port)

  TestData.create(calendarsActor)

  def receive = runRoute(routes) orElse {
    case Bound(address)     ⇒ println(s"API is bound to $address.")
    case CommandFailed(cmd) ⇒ throw new ApiNotBoundException("Not bound.", cmd)
  }
}

trait Services extends DefaultCalendarsActorProvider
  with DefaultAskTimeoutProvider

trait Routes extends HttpService
    with TaskApiRoutes
    with ResourceApiRoutes
    with ScheduleApiRoutes
    with CalendarApiRoutes
    with CounterApiRoutes
    with CORSSupport {

  val routes =
    cors {
      calendarApiRoutes
    }
}

class ApiNotBoundException(msg: String, command: Command) extends Exception(msg)
