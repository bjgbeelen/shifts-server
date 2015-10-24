package com.besquare
package shifts
package schedule

import common._

import spray.json._

class ScheduleMarshallingSpec extends UnitSpecLike with ScheduleMarshalling {

  val randomId = Id.random
  val constraints = ResourceConstraints(
    resourceId = randomId,
    absence = Set.empty,
    desiredNumberOfTasks = Map.empty,
    desiredNumberOfTasksInOneWeekend = 2,
    wantsCoupledTasks = true,
    wantsEveningNightCombination = true
  )
  val parsedJson = JsonParser(s"""{
        "resourceId": "$randomId",
        "absence": [],
        "desiredNumberOfTasks": {},
        "desiredNumberOfTasksInOneWeekend": 2,
        "wantsCoupledTasks": true,
        "wantsEveningNightCombination": true
      }""")

  "ResourceConstraintsFormat" should {
    "construct a JSON AST" in {
      constraints.toJson shouldEqual parsedJson
    }

    "construct a ResourceConstraints object from json" in {
      parsedJson.convertTo[ResourceConstraints] shouldEqual constraints
    }
  }
}
