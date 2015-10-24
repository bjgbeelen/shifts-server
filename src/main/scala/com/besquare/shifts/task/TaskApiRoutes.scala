package com.besquare
package shifts
package task

import akka.actor._
import akka.pattern._

import spray.json._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.routing._

import common._
import calendar._
import Calendar._

trait TaskApiRoutesProvider extends Directives {
  def taskApiRoutes(calendar: ActorRef): RequestContext ⇒ Unit
}

trait TaskApiRoutes extends TaskApiRoutesProvider
    with AskTimeoutProvider
    with TaskMarshalling
    with ActorRefFactoryExecutionContextProvider {
  def taskApiRoutes(calendar: ActorRef) = {
    // format: OFF
    pathPrefix("tasks") {
      pathEnd {
        get {
          onSuccess(calendar ? Calendar.GetTasks) {
            case GotTasks(tasks) => complete(tasks)
          }
        } ~
        (post & entity(as[Calendar.CreateTasks])) { cmd =>
          calendar ! cmd
          complete("create tasks")
        }
      }
    }
    // format: ON
  }
}
