package com.besquare
package shifts
package user

import akka.actor._

import common._
import calendar._

object UserActor {
  def props(userId: Id) = Props(new UserActor(userId))
  def name(userId: Id) = s"user-actor-$userId"

  trait Command {
    val userId: Id
  }
}

class UserActor(userId: Id) extends Actor with ActorCreationSupportForActors {
  def receive: Receive = {
    case _ ⇒ //case cc: CalendarActor.Command ⇒ getOrCreateChild(CalendarsActor.props, CalendarsActor.name)
  }
}
