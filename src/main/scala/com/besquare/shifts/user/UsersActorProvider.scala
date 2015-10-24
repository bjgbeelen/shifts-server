package com.besquare
package shifts
package user

import akka.actor._

import common._

trait UsersActorProvider {
  val usersActor: ActorRef
}

trait DefaultUsersActorProvider extends UsersActorProvider with ActorRefFactoryProvider {
  val usersActor = Users(actorSystem)
}
