package com.besquare
package shifts
package calendar
package marshalling

import spray.httpx.SprayJsonSupport._
import spray.json._

trait CalendarViewMarshalling extends DefaultJsonProtocol {

  implicit object DayFormat extends RootJsonFormat[Day] {
    def read(json: JsValue) = deserializationError(s"Cannot deserialize Day: invalid input. Raw input: " + json.compactPrint)

    def write(item: Day) = JsObject(Map(
      "label" -> item.label.toJson,
      "day" -> item.number.toJson,
      "id" -> item.id.toJson,
      "dayOfWeek" -> item.dayOfWeek.toJson
    ))
  }

  implicit object PartialWeekFormat extends RootJsonFormat[PartialWeek] {
    def read(json: JsValue) = deserializationError(s"Cannot deserialize PartialWeek: invalid input. Raw input: " + json.compactPrint)

    def write(item: PartialWeek) = JsObject(Map(
      "week" -> item.number.toJson,
      "days" -> item.days.toJson
    ))
  }

  implicit object MonthFormat extends RootJsonFormat[Month] {
    def read(json: JsValue) = deserializationError(s"Cannot deserialize Month: invalid input. Raw input: " + json.compactPrint)

    def write(item: Month) = JsObject(Map(
      "month" -> item.number.toJson,
      "name" -> item.name.toJson,
      "weeks" -> item.weeks.toJson
    ))
  }

  implicit object YearFormat extends RootJsonFormat[Year] {
    def read(json: JsValue) = deserializationError(s"Cannot deserialize Year: invalid input. Raw input: " + json.compactPrint)

    def write(item: Year) = JsObject(Map(
      "year" -> item.number.toJson,
      "months" -> item.months.toJson
    ))
  }

  implicit object CalendarViewFormat extends RootJsonFormat[CalendarView] {
    def read(json: JsValue) = deserializationError(s"Cannot deserialize CalendarView: invalid input. Raw input: " + json.compactPrint)

    def write(item: CalendarView) = JsObject(Map(
      "name" -> item.name.toJson,
      "years" -> item.years.toJson
    ))
  }

}
