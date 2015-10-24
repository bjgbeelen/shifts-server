package com.besquare
package shifts
package calendar

import akka.actor._
import akka.persistence._
import com.github.nscala_time.time.Imports._

import common._
import task._
import schedule._
import resource._
import counter._

object Calendar {
  def props(name: String) = Props(new Calendar(name))
  def name(name: String) = s"calendar-actor-$name"

  sealed trait Command
  case class CreateView(from: DateTime, to: DateTime, dayLabels: Map[DayId, String] = Map.empty) extends Command
  case class CreateTasks(label: String, start: Minute, end: Minute, tags: Set[Tag], filters: Seq[DaySelection]) extends Command
  case class CreateSchedule(name: String, assignments: Map[TaskId, ResourceId], resourceConstraints: Seq[ResourceConstraints])
  case class AddResource(resource: Resource) extends Command
  case class AddCounter(counter: Counter) extends Command
  case class AddTags(tags: Set[Tag], filters: Seq[TaskSelection]) extends Command
  case object GetView extends Command
  case object GetTasks extends Command
  case object GetCounters extends Command
  case object GetResources extends Command
  case object GetSchedules extends Command
  case object GetCalendarState extends Command
  case class GetSchedule(name: String) extends Command

  sealed trait Event
  case class ViewCreated(view: CalendarView) extends Event
  case class TasksCreated(tasks: Seq[Task]) extends Event
  case class ResourceAdded(resource: Resource) extends Event
  case class CounterAdded(counter: Counter) extends Event
  case class ScheduleCreated(name: String) extends Event
  case class TasksUpdated(tasks: Seq[Task]) extends Event
  case class GotView(view: CalendarView) extends Event
  case class GotTasks(tasks: Map[DayId, Seq[Task]]) extends Event
  case class GotCounters(counters: Set[Counter]) extends Event
  case class GotResources(resources: Set[Resource]) extends Event
  case class GotSchedules(names: Seq[String]) extends Event
  case class GotSchedule(schedule: ActorRef) extends Event
  case class GotCalendarState(state: State) extends Event
  case object ScheduleWasNotFound extends Event
  case object ScheduleAlreadyExists extends Event

  case class State(view: CalendarView, tasks: Map[DayId, Seq[Task]], resources: Set[Resource], schedules: Seq[String], counters: Set[Counter]) {
    def addTasks(t: Seq[Task]) = copy(tasks = tasks merge t)
    def updateTasks(t: Seq[Task]) = copy(tasks = tasks replace t)
    def addSchedule(name: String) = copy(schedules = schedules :+ name)
    def addResource(resource: Resource) = copy(resources = resources + resource)
    def addCounter(counter: Counter) = copy(counters = counters + counter)
  }

  object State {
    lazy val Empty = State(CalendarView.Empty)
    def apply(view: CalendarView): State = State(view, Map.empty, Set.empty, Seq.empty, Set.empty)
  }
}

class Calendar(name: String) extends PersistentActor
    with ActorCreationSupportForActors {
  import Calendar._

  var state = State.Empty

  override val persistenceId = s"calendar-actor-$name"

  def receiveCommand = uninitialized

  def uninitialized: Receive = {
    case CreateView(from, to, dayLabels) ⇒
      persist(ViewCreated(CalendarView(name, from, to, dayLabels))) { event ⇒
        sender() ! event
        updateState(event)
      }
    case _ ⇒
  }

  def initialized: Receive = {
    case CreateTasks(label, start, end, tags, selection) ⇒
      val days = state.view.filter(selection)
      val tasks = days.map { day ⇒ Task(day, label, start, end, tags) }
      val event = TasksCreated(tasks)
      sender() ! event
      persist(event) { e ⇒
        updateState(e)
      }
    case CreateSchedule(name, _, _) if state.schedules.contains(name) ⇒
      val event = ScheduleAlreadyExists
      sender() ! event
    case AddResource(resource) ⇒
      val event = ResourceAdded(resource)
      sender() ! event
      persist(event) { e ⇒
        updateState(e)
      }
    case AddCounter(counter) ⇒
      val event = CounterAdded(counter)
      sender() ! event
      persist(event) { e ⇒
        updateState(e)
      }
    case CreateSchedule(name, assignments, constraints) ⇒
      val schedule = getOrCreateScheduleActor(name)
      schedule ! ScheduleActor.Initialize(assignments, constraints)
      val event = ScheduleCreated(name)
      sender() ! event
      persist(event) { e ⇒
        updateState(e)
      }
    case AddTags(tags, selection) ⇒
      val tasks = state.tasks.select(selection).map(_.addTags(tags))
      val event = TasksUpdated(tasks)
      sender() ! event
      persist(event) { e ⇒
        updateState(e)
      }
    case GetSchedules                                        ⇒ sender() ! GotSchedules(state.schedules)
    case GetSchedule(name) if state.schedules.contains(name) ⇒ sender() ! GotSchedule(getOrCreateScheduleActor(name))
    case GetSchedule(name)                                   ⇒ sender() ! ScheduleWasNotFound
    case GetView                                             ⇒ sender() ! GotView(state.view)
    case GetCounters                                         ⇒ sender() ! GotCounters(state.counters)
    case GetTasks                                            ⇒ sender() ! GotTasks(state.tasks)
    case GetResources                                        ⇒ sender() ! GotResources(state.resources)
    case GetCalendarState                                    ⇒ sender() ! GotCalendarState(state)
  }

  override def receiveRecover: Receive = {
    case e: Event ⇒ updateState(e)
  }

  def updateState(e: Event) = e match {
    case ViewCreated(view) ⇒
      state = State(view)
      context become initialized
    case TasksCreated(tasks)       ⇒ state = state addTasks tasks
    case ResourceAdded(resource)   ⇒ state = state addResource resource
    case CounterAdded(counter)     ⇒ state = state addCounter counter
    case ScheduleCreated(schedule) ⇒ state = state addSchedule schedule
    case TasksUpdated(tasks)       ⇒ state = state updateTasks tasks
    case _                         ⇒ println("unexepcted thing happening")
  }

  private def getOrCreateScheduleActor(scheduleName: String) = getOrCreateChild(ScheduleActor.props(self, name, scheduleName), ScheduleActor.name(name, scheduleName))
}
