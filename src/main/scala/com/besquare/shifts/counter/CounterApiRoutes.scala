package com.besquare
package shifts
package counter

import akka.actor._
import akka.pattern._
import spray.json._
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport._
import spray.routing._

import common._
import calendar._

trait CounterApiRoutesProvider extends Directives {
  def counterApiRoutes(calendar: ActorRef): RequestContext ⇒ Unit
}

trait CounterApiRoutes extends CounterApiRoutesProvider
    with CounterMarshalling
    with AskTimeoutProvider
    with ActorRefFactoryExecutionContextProvider {
  def counterApiRoutes(calendar: ActorRef) = {
    // format: OFF
    path("counters") {
      get {
        onSuccess(calendar ? Calendar.GetCounters) {
          case Calendar.GotCounters(counters) =>  complete(counters)
        }
      }
    }
    // format: ON
  }
}
