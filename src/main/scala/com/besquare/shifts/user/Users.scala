package com.besquare
package shifts
package user

import akka.actor._

object Users {
  def apply(system: ActorSystem): ActorRef = UsersExtension(system.asInstanceOf[ExtendedActorSystem]).actorRef
}

private[user] object UsersExtension extends ExtensionKey[UsersExtension]
private[user] class UsersExtension(system: ExtendedActorSystem) extends Extension {
  val actorRef = system.actorOf(UsersActor.props, UsersActor.name)
}
