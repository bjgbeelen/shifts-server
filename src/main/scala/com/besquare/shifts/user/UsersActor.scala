package com.besquare
package shifts
package user

import akka.actor._

import common._
import calendar._

object UsersActor {
  val props = Props(new UsersActor())
  val name = "shifts-users"
}

class UsersActor extends Actor with ActorCreationSupportForActors {
  def receive: Receive = {
    case uc: UserActor.Command ⇒ getOrCreateChild(UserActor.props(uc.userId), UserActor.name(uc.userId)) forward uc
  }
}
