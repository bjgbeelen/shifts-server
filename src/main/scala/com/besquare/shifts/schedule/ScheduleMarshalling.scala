package com.besquare
package shifts
package schedule

import spray.httpx.SprayJsonSupport._
import spray.json._

import ScheduleActor._
import IdMarshalling._

trait ScheduleMarshalling extends DefaultJsonProtocol {
  implicit val resourceConstraintsFormat = jsonFormat6(ResourceConstraints.apply)
  implicit val stateFormat = jsonFormat2(State.apply)
}
