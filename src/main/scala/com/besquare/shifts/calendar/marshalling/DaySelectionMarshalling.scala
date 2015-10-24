package com.besquare
package shifts
package calendar
package marshalling

import spray.httpx.SprayJsonSupport._
import spray.json._

trait DaySelectionMarshalling extends DefaultJsonProtocol {
  implicit val weekDaySelectionFormat = jsonFormat1(WeekDaySelection.apply)
  implicit val periodSelectionFormat = jsonFormat2(PeriodSelection.apply)
  implicit val dayIdSelectionFormat = jsonFormat1(DayIdSelection.apply)

  implicit object DaySelectionFormat extends RootJsonFormat[DaySelection] {
    def read(json: JsValue) = json.asJsObject.getFields("dayOfWeeks", "from", "to") match {
      case Seq(dayOfWeeks) ⇒ WeekDaySelection(dayOfWeeks.convertTo[Seq[DayOfWeekNumber]])
      case Seq(from, to)   ⇒ PeriodSelection(from.convertTo[DayId], to.convertTo[DayId])
      case _               ⇒ deserializationError(s"Cannot deserialize Dayselection: invalid input. Raw input: " + json.compactPrint)
    }

    def write(item: DaySelection) = item match {
      case x: WeekDaySelection ⇒ x.toJson
      case x: PeriodSelection  ⇒ x.toJson
      case x: DayIdSelection   ⇒ x.toJson
    }
  }
}
