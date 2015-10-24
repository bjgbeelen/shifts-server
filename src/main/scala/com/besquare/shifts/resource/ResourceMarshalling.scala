package com.besquare
package shifts
package resource

import spray.httpx.SprayJsonSupport._
import spray.json._

import IdMarshalling._
import calendar.Day

trait ResourceMarshalling extends DefaultJsonProtocol {
  implicit object GPFormat extends RootJsonFormat[GP] {
    def read(json: JsValue) = deserializationError(s"Cannot deserialize GP: invalid input. Raw input: " + json.compactPrint)

    def write(item: GP) = JsObject(Map(
      "id" -> item.id.toJson,
      "name" -> item.name.toJson,
      "numberOfPatients" -> item.numberOfPatients.toJson
    ))
  }

  implicit object ResourceFormat extends RootJsonFormat[Resource] {
    def read(json: JsValue) = deserializationError(s"Cannot deserialize Resource: invalid input. Raw input: " + json.compactPrint)

    def write(item: Resource) = item match {
      case a: GP ⇒ a.toJson
    }
  }

}
