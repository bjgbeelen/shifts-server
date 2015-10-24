package com.besquare
package shifts
package resource

import akka.actor._
import akka.pattern._

import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.routing._

import common._
import calendar._
import IdMarshalling._

trait ResourceApiRoutesProvider extends Directives {
  def resourceApiRoutes(calendar: ActorRef): RequestContext ⇒ Unit
}

trait ResourceApiRoutes extends ResourceApiRoutesProvider
    with ResourceMarshalling
    with AskTimeoutProvider
    with ActorRefFactoryExecutionContextProvider {
  def resourceApiRoutes(calendar: ActorRef) = {
    // format: OFF
    pathPrefix("resources") {
      pathEnd {
        onSuccess(calendar ? Calendar.GetResources) {
          case Calendar.GotResources(resources) => {
            val resourceMap = resources.map{ case resource @ GP(id, name, _) => (id, resource)}.toMap
            complete(resourceMap)
          } 
        }
      }
    }
    // format: ON
  }
}
