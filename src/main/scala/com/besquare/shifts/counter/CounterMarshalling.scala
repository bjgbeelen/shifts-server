package com.besquare
package shifts
package counter

import spray.httpx.SprayJsonSupport._
import spray.json._

import IdMarshalling._
import task._

trait CounterMarshalling extends DefaultJsonProtocol {
  implicit object CounterFormat extends RootJsonFormat[Counter] {
    def read(json: JsValue) = deserializationError(s"Cannot deserialize Counter: invalid input. Raw input: " + json.compactPrint)

    def write(item: Counter) = JsObject(
      "id" -> item.id.toJson,
      "name" -> item.name.toJson,
      "include" -> item.include.toJson,
      "exclude" -> item.exclude.toJson,
      "children" -> item.children.toJson
    )
  }
}
