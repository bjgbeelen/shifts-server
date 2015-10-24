package com.besquare
package shifts

import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._

object IdMarshalling {
  implicit object IdFormat extends RootJsonFormat[Id] {
    def read(json: JsValue) = Id(json.convertTo[String])

    def write(item: Id) = JsString(item.value)
  }
}
