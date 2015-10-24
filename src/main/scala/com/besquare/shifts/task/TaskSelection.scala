package com.besquare
package shifts
package task

import calendar._

abstract class TaskSelection {
  def exec: Task ⇒ Boolean
}

case class IncludeTags(include: Seq[Tag]) extends TaskSelection {
  def exec = task ⇒ include.forall(task.tags.contains(_))
}
case class ExcludeTags(exclude: Seq[Tag]) extends TaskSelection {
  def exec = task ⇒ exclude.forall(!task.tags.contains(_))
}
case class TaskOnDaySelection(dayId: DayId) extends TaskSelection {
  def exec = task ⇒ task.dayId == dayId
}
case class TaskNotOnDaySelection(days: Seq[DayId]) extends TaskSelection {
  def exec = task ⇒ !(task.dayId in days)
}
