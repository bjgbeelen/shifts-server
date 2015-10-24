package com.besquare
package shifts
package schedule

import scala.util._

import akka.persistence._
import akka.pattern._
import akka.actor._

import common._
import task._
import calendar._
import resource._

object ScheduleActor {
  def props(calendar: ActorRef, calendarName: String, name: String) = Props(new ScheduleActor(calendar, calendarName, name))
  def name(calendarName: String, name: String) = s"schedule-actor-$calendarName-$name"

  sealed trait Command
  case class Initialize(assignments: Map[TaskId, ResourceId], resourceConstraints: Seq[ResourceConstraints]) extends Command
  case class AddAssignment(assignment: Assignment) extends Command
  case class RemoveAssignment(taskId: TaskId) extends Command
  case class UpdateResourceConstraints(resourceConstraints: ResourceConstraints) extends Command
  case object Solve extends Command
  case object GetModelData extends Command
  case object GetState extends Command

  sealed trait Event
  case class Initialized(assignments: Map[TaskId, ResourceId], resourceConstraints: Seq[ResourceConstraints]) extends Event
  case class AssignmentAdded(assignment: Assignment) extends Event
  case class AssignmentRemoved(taskId: TaskId) extends Event
  case class ResourceConstraintsUpdated(resourceConstraints: ResourceConstraints) extends Event
  case class GotState(state: State) extends Event

  case class State(assignments: Map[TaskId, ResourceId], resourceConstraints: Seq[ResourceConstraints]) {
    def addAssigmment(a: Assignment) = copy(assignments + a)
    def removeAssignment(t: TaskId) = copy(assignments - t)
    def updateResourceConstraints(constraints: ResourceConstraints) = copy(resourceConstraints =
      resourceConstraints.filter(_.resourceId != constraints.resourceId) :+ constraints)
  }
  object State {
    def Empty = State(Map.empty, Seq.empty)
  }
}

class ScheduleActor(calendar: ActorRef, calendarName: String, name: String) extends PersistentActor
    with DefaultAskTimeoutProvider
    with ActorExecutionContextProvider {
  import ScheduleActor._

  var state: State = State.Empty
  override val persistenceId = ScheduleActor.name(calendarName, name)

  def receiveCommand = uninitialized

  def uninitialized: Receive = {
    case Initialize(assignments, constraints) ⇒
      val event = Initialized(assignments, constraints)
      sender() ! event
      persist(event) { e ⇒
        updateState(e)
      }
    case _ ⇒
  }

  def initialized: Receive = {
    case AddAssignment(assignment) ⇒
      val event = AssignmentAdded(assignment)
      sender() ! event
      persist(event) { e ⇒
        updateState(e)
      }
    case RemoveAssignment(taskId) ⇒
      val event = AssignmentRemoved(taskId)
      sender() ! event
      persist(event) { e ⇒
        updateState(e)
      }
    case UpdateResourceConstraints(constraints) ⇒
      val event = ResourceConstraintsUpdated(constraints)
      sender() ! event
      persist(event) { e ⇒
        updateState(e)
      }
    case GetState ⇒ sender() ! GotState(state)
    case Solve ⇒
      (calendar ? Calendar.GetTasks).onComplete {
        case Success(Calendar.GotTasks(taskMap)) ⇒
          context.actorOf(ScheduleSolverActor.props(calendar, taskMap.values.toList.flatten, state.resourceConstraints), ScheduleSolverActor.name) ! ScheduleSolverActor.Start
        case Failure(e) ⇒ println(e)
      }
    case GetModelData ⇒
      val originalSender = sender()
      (calendar ? Calendar.GetCalendarState).onComplete {
        case Success(Calendar.GotCalendarState(calendar)) ⇒
          val relevantTasks = calendar.tasks.values.toList.flatten.filter(!_.tags.contains("ignore")).filter(_.tags.contains("weekend"))
          val tasks = relevantTasks.map(_.descriptiveId).mkString(" ")
          val resources = calendar.resources.map(_.name.replace(" ", "_")).mkString(" ")
          val days = relevantTasks.map(_.dayId).toSet.mkString(" ")
          val weeks = calendar.view.weeks.map(_.id).toSet.mkString(" ")
          val tasksInWeek = calendar.view.weeks.map { week ⇒
            val tasks = relevantTasks.filter(_.dayId in week.days.map(_.id))
            tasks.map(_.descriptiveId).mkString(s"set DIENSTEN_IN_WEEK[${week.id}] := ", " ", "")
          }.mkString("\n")

          val result = s"""
            |set ARTSEN := $resources
            |
            |set DIENSTEN := $tasks
            |
            |set DAGEN := $days
            |
            |set WEKEN := $weeks
            |
            |$tasksInWeek
          """.stripMargin

          originalSender ! result
        case Failure(e) ⇒ println(e)
      }
    case _ ⇒
  }

  override def receiveRecover: Receive = {
    case e: Event ⇒ updateState(e)
  }

  private def updateState(event: Event) = event match {
    case Initialized(assignments, constraints) ⇒
      state = State(assignments, constraints)
      context become initialized
    case AssignmentAdded(assignment)             ⇒ state = state addAssigmment assignment
    case AssignmentRemoved(taskId)               ⇒ state = state removeAssignment taskId
    case ResourceConstraintsUpdated(constraints) ⇒ state = state updateResourceConstraints constraints
    case _                                       ⇒
  }
}
