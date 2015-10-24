package com.besquare
package shifts
package calendar

import akka.actor._

object Calendars {
  def apply(system: ActorSystem): ActorRef = CalendarsExtension(system.asInstanceOf[ExtendedActorSystem]).actorRef
}

private[calendar] object CalendarsExtension extends ExtensionKey[CalendarsExtension]
private[calendar] class CalendarsExtension(system: ExtendedActorSystem) extends Extension {
  val actorRef = system.actorOf(CalendarsActor.props, CalendarsActor.name)
}
