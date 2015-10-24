package com.besquare
package common

import akka.actor._

trait ActorCreationSupport {
  def createChild(props: Props, name: String): ActorRef
  def getChild(name: String): Option[ActorRef]
  def getOrCreateChild(props: Props, name: String): ActorRef = getChild(name).getOrElse(createChild(props, name))
}

trait ActorCreationSupportForActors extends ActorCreationSupport {
  this: Actor ⇒

  def createChild(props: Props, name: String): ActorRef = context.actorOf(props, name)
  def getChild(name: String) = context.child(name)
}
