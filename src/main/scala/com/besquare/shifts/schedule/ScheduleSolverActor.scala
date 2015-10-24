package com.besquare
package shifts
package schedule

import akka.actor._

import oscar.linprog.modeling._
import oscar.linprog._
import oscar.algebra._

import resource._
import task._

object ScheduleSolverActor {
  def props(calendar: ActorRef, tasks: Seq[Task], resourceConstraints: Seq[ResourceConstraints]) =
    Props(new ScheduleSolverActor(calendar, tasks, resourceConstraints))
  def name = "schedule-solver-actor" + Id.random

  case object Start
}

class ScheduleSolverActor(calendar: ActorRef, tasks: Seq[Task], resourceConstraints: Seq[ResourceConstraints]) extends Actor {
  import ScheduleSolverActor._

  lazy val taskIds = tasks.filter(_.tags.contains("weekend")).map(_.id)
  lazy val resourceIds = resourceConstraints.map(_.resourceId)

  def receive: Receive = {
    case Start ⇒
      println(taskIds.size, resourceConstraints.size)
    //optimize
  }

  def optimize = {
    implicit val mip = MIPSolver()

    /*************** VARIABLES **********************/
    val assignments: Map[(TaskId, ResourceId), MIPIntVar] = {
      for {
        taskId ← taskIds;
        resourceId ← resourceIds
      } yield (taskId, resourceId) -> MIPIntVar(s"assignment-$taskId-$resourceId", 0 to 1)
    }.toMap

    /***************** GOAL **************************/
    val startLE: LinearExpression = 0
    minimize(
      {
        for {
          taskId ← taskIds;
          resourceId ← resourceIds
        } yield assignments(taskId, resourceId)
      }.foldLeft(startLE)(_ + _)
    )

    /***************** CONSTRAINTS ********************/

    // every task should be assigned exactly once
    for { taskId ← taskIds }
      add(
        (sum(resourceIds)(resourceId ⇒ assignments(taskId, resourceId))).==(1)
      )

    start()

    println("SOLVING!", objectiveValue.get)

    //release()
  }
}
