package com.besquare
package shifts
package calendar
package marshalling

import spray.httpx.SprayJsonSupport._
import spray.json._
import Calendar._

trait CalendarMarshalling extends DefaultJsonProtocol {
  implicit object CreateCalendarFormat extends RootJsonFormat[CalendarsActor.CreateCalendar] {
    def read(json: JsValue) = json.asJsObject.getFields("name", "from", "to", "dayLabels") match {
      case Seq(name, from, to, dayLabels) ⇒ CalendarsActor.CreateCalendar(
        name = name.convertTo[String],
        from = DateTime(from.convertTo[String]),
        to = DateTime(to.convertTo[String]),
        dayLabels = dayLabels.convertTo[Map[DayId, String]]
      )
      case _ ⇒ deserializationError(s"Cannot deserialize CreateView: invalid input. Raw input: " + json.compactPrint)
    }

    def write(item: CalendarsActor.CreateCalendar) = serializationError(s"Cannot serialize Calendar: invalid input. Input: " + item)
  }
}
