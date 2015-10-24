package com.besquare
package shifts
package calendar

import scala.concurrent.duration._
import scala.util._
import akka.util._

import akka.persistence._
import akka.actor._
import akka.pattern._
import com.github.nscala_time.time.Imports._

import common._

object CalendarsActor {
  def props = Props(new CalendarsActor())
  def name = s"calendars-actor"

  sealed trait Command
  case class CreateCalendar(name: String, from: DateTime, to: DateTime, dayLabels: Map[DayId, String]) extends Command
  case class PersistCalendarCreated(name: String, originalSender: ActorRef) extends Command
  case class GetCalendar(name: String) extends Command
  case object GetAvailableCalendars extends Command

  sealed trait Event
  case class GotCalendar(calendar: ActorRef) extends Event
  case class CalendarCreated(name: String) extends Event
  case class GotAvailableCalendars(calendars: Set[String]) extends Event
  case object CalendarWasNotFound extends Event
  case object CalendarDidAlreadyExist extends Event
}

class CalendarsActor extends PersistentActor
    with ActorCreationSupportForActors
    with DefaultAskTimeoutProvider {
  import CalendarsActor._

  var state: Set[String] = Set.empty

  val persistenceId = CalendarsActor.name

  implicit val ex = context.dispatcher

  def receiveCommand: Receive = {
    case CreateCalendar(name, _, _, _) if state.contains(name) ⇒ sender() ! CalendarDidAlreadyExist
    case PersistCalendarCreated(name, originalSender) ⇒
      persist(CalendarCreated(name)) { e ⇒
        originalSender ! e
        updateState(e)
      }
    case CreateCalendar(name, from, to, dayLabels) ⇒
      println("incoming create calenar request")
      val originalSender = sender()
      (getOrCreateCalendarActor(name) ? Calendar.CreateView(from, to, dayLabels)).onComplete {
        case Success(Calendar.ViewCreated(_)) ⇒
          self ! PersistCalendarCreated(name, originalSender)
        case Success(_)  ⇒ println("An unexpected successful response was found")
        case Failure(ex) ⇒ println(s"Something went wrong during view creation: $ex")
      }
    case GetCalendar(name) if state.contains(name) ⇒ sender() ! GotCalendar(getOrCreateCalendarActor(name))
    case GetCalendar(name)                         ⇒ sender() ! CalendarWasNotFound
    case GetAvailableCalendars                     ⇒ sender() ! GotAvailableCalendars(state)
  }

  override def receiveRecover: Receive = {
    case e: Event ⇒ updateState(e)
  }

  private def getOrCreateCalendarActor(name: String) =
    getOrCreateChild(Calendar.props(name), Calendar.name(name))

  private def updateState(e: Event) = e match {
    case CalendarCreated(name) ⇒
      state = state + name;
    case _ ⇒
  }
}
