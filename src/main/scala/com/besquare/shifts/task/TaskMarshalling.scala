package com.besquare
package shifts
package task

import spray.httpx.SprayJsonSupport._
import spray.json._

import IdMarshalling._
import calendar._
import Calendar._
import marshalling._

trait TaskMarshalling extends DefaultJsonProtocol
    with DaySelectionMarshalling {
  implicit val taskFormat = jsonFormat6(Task.apply)
  implicit val createTasksFormat = jsonFormat5(CreateTasks.apply)
}
