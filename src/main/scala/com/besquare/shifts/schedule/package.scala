package com.besquare
package shifts

import calendar._
import task._
import resource._

package object schedule {
  type Assignment = (TaskId, ResourceId)
  case class ResourceConstraints(
    resourceId: ResourceId,
    desiredNumberOfTasks: Map[Id, Int],
    desiredNumberOfTasksInOneWeekend: Int,
    wantsEveningNightCombination: Boolean,
    wantsCoupledTasks: Boolean,
    absence: Set[DayId] = Set.empty)
}
