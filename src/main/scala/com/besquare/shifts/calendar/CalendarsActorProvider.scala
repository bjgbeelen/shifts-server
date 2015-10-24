package com.besquare
package shifts
package calendar

import akka.actor._

import common._

trait CalendarsActorProvider {
  val calendarsActor: ActorRef
}

trait DefaultCalendarsActorProvider extends CalendarsActorProvider with ActorRefFactoryProvider {
  val calendarsActor = Calendars(actorSystem)
}
