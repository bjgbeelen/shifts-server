package com.besquare
package shifts

import calendar._

package object task {
  type Hour = Int
  type Minute = Int
  type Tag = String

  implicit class IntAsTime(minutes: Int) {
    def ::(hours: Int) = hours * 60 + minutes
  }

  type TaskId = Id

  case class Task(id: TaskId, dayId: DayId, label: String, start: Minute, end: Minute, tags: Set[Tag]) {
    require(start < end, "The start of the task should be earlier than its end")

    def updateLabel(label: String) = copy(label = label)
    def updateStart(start: Minute) = copy(start = start)
    def updateEnd(end: Minute) = copy(end = end)
    def addTags(newTags: Set[Tag]) = copy(tags = tags ++ newTags)

    def descriptiveId = dayId + tags.mkString("_", "_", "")
  }

  implicit class TaskMerger(map: Map[DayId, Seq[Task]]) {
    def merge(task: Task): Map[DayId, Seq[Task]] = map + (task.dayId -> (map.getOrElse(task.dayId, Seq.empty) :+ task))
    def replace(task: Task): Map[DayId, Seq[Task]] = map + (task.dayId -> (map.getOrElse(task.dayId, Seq.empty).filter(_.id != task.id) :+ task))

    def merge(seq: Seq[Task]): Map[DayId, Seq[Task]] = seq.foldLeft(map) {
      case (oldMap, task) ⇒ oldMap.merge(task)
    }

    def replace(seq: Seq[Task]): Map[DayId, Seq[Task]] = seq.foldLeft(map) {
      case (oldMap, task) ⇒ oldMap.replace(task)
    }

    def select(selection: Seq[TaskSelection]) = selection.foldLeft(map.values.toList.flatten) {
      case (tasks, selection) ⇒ tasks.filter(selection.exec)
    }
  }

  case object Task {
    def apply(day: Day, label: String, start: Minute, end: Minute, tags: Set[Tag] = Set.empty): Task = Task(
      Id.random,
      day.id,
      label,
      start,
      end,
      tags
    )
  }

}
