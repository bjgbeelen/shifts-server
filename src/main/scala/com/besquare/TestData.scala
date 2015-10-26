package com.besquare
package shifts

import scala.concurrent._
import scala.util._

import akka.util._
import akka.actor._
import akka.pattern._

import calendar._
import CalendarsActor._
import Calendar._
import resource._
import task._
import schedule._
import counter._

object TestData {

  //val calendarView = CalendarView("iets", DateTime("2015-12-28"), DateTime("2017-01-08"), Map.empty)

  val resources = Seq(
    GP(name = "Acker, vd", numberOfPatients = 1518),
    GP(name = "Ambachtsheer", numberOfPatients = 2297),
    GP(name = "Baars", numberOfPatients = 3411),
    GP(name = "Beelen", numberOfPatients = 2553),
    GP(name = "Daamen", numberOfPatients = 2276),
    GP(name = "Dooren, van", numberOfPatients = 2720),
    GP(name = "Gielen", numberOfPatients = 2862),
    GP(name = "Heeden, vd", numberOfPatients = 2276),
    GP(name = "HeHo", numberOfPatients = 2450),
    GP(name = "Hoeks", numberOfPatients = 2686),
    GP(name = "Homa", numberOfPatients = 2601),
    GP(name = "Houppermans", numberOfPatients = 2041),
    GP(name = "Marcelis", numberOfPatients = 1700),
    GP(name = "Nierop, van", numberOfPatients = 1701),
    GP(name = "Onderwater", numberOfPatients = 2024),
    GP(name = "Pruijssen", numberOfPatients = 1700),
    GP(name = "Rekkers", numberOfPatients = 2648),
    GP(name = "Rens, van", numberOfPatients = 2276),
    GP(name = "Sluijs, vd", numberOfPatients = 2774)
  )

  val counters = Seq(
    // Counter("totaal", include = Seq.empty, exclude = Seq("ignore"), children = Seq(
    //   Counter("week", include = Seq("week")),
    //   Counter("weekend", include = Seq("weekend")),
    //   Counter("nacht", include = Seq("nacht"))
    // )),
    Counter("week", include = Seq("week"), exclude = Seq("ignore"), children = Seq(
      Counter(name = "consult", include = Seq("consult")),
      Counter(name = "visite", include = Seq("visite")),
      Counter(name = "nacht", include = Seq("nacht"))
    )),
    Counter("weekend", include = Seq("weekend"), exclude = Seq("ignore"), children = Seq(
      Counter(name = "consult", include = Seq("consult")),
      Counter(name = "visite", include = Seq("visite")),
      Counter(name = "nacht", include = Seq("nacht")),
      Counter(name = "feest", include = Seq("feest"))
    ))
  )

  val resourceConstraints = resources.map {
    case GP(id, "Acker, vd", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 5)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 5)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 7)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 7)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 1)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Ambachtsheer", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 8)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 8)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 10)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 10)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 2)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Baars", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 12)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 12)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 15)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 15)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 3)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Beelen", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 8)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 9)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 12)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 12)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Daamen", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 8)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 7)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 11)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 10)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Dooren, van", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 9)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 10)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 12)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 12)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 2)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Gielen", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 10)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 10)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 12)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 13)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 2)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Heeden, vd", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 8)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 7)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 11)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 10)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "HeHo", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 8)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 8)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 12)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 11)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Hoeks", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 9)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 9)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 12)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 12)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 2)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Homa", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 8)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 9)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 11)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 12)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 2)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Houppermans", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 7)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 7)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 9)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 9)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Marcelis", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 6)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 5)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 7)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 8)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Nierop, van", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 6)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 5)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 7)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 8)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Pruijssen", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 5)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 6)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 7)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 8)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Onderwater", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 7)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 7)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 9)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 9)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Rens, van", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 8)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 8)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 10)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 10)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 1)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Rekkers", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 8)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 9)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 12)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 12)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 2)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 2)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
    case GP(id, "Sluijs, vd", patients) ⇒ ResourceConstraints(id, desiredNumberOfTasks = counters.foldLeft(Map[Id, Int]()) {
      case (map, Counter(_, "week", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 9)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 9)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 4)
      }.toMap
      case (map, Counter(_, "weekend", _, _, children)) ⇒ map ++ children.map {
        case Counter(id, "consult", _, _, _) ⇒ (id, 13)
        case Counter(id, "visite", _, _, _)  ⇒ (id, 12)
        case Counter(id, "nacht", _, _, _)   ⇒ (id, 2)
        case Counter(id, "feest", _, _, _)   ⇒ (id, 3)
      }.toMap
      case (map, _) ⇒ map
    },
      desiredNumberOfTasksInOneWeekend = 2,
      wantsEveningNightCombination = false,
      wantsCoupledTasks = false
    )
  }

  val holidays = Seq(
    ("20151231", "Oudjaarsavond", false),
    ("20160101", "Nieuwjaarsdag", true),
    ("20160325", "", false),
    ("20160326", "", true),
    ("20160327", "1e paasdag", true),
    ("20160328", "2e paasdag", true),
    ("20160426", "Koningsnacht", false),
    ("20160427", "Koningsdag", true),
    ("20160504", "", false),
    ("20160505", "Hemelvaart", true),
    ("20160513", "", false),
    ("20160514", "", true),
    ("20160515", "1e pinksterdag", true),
    ("20160516", "2e pinksterdag", true),
    ("20161224", "Kerstavond", false),
    ("20161225", "1e kerstdag", true),
    ("20161226", "2e kerstdag", true),
    ("20161231", "Oudjaarsavond", false),
    ("20170101", "Nieuwjaarsdag", true)
  )

  val ignoreTasks = Seq(
    Seq("20151228", "nacht"),
    Seq("20151228", "avond", "consult"),
    Seq("20151228", "avond", "visite"),
    Seq("20151229", "nacht"),
    Seq("20151229", "avond", "consult"),
    Seq("20151229", "avond", "visite"),
    Seq("20151230", "nacht"),
    Seq("20151230", "avond", "consult"),
    Seq("20151230", "avond", "visite"),
    Seq("20151231", "nacht"),
    Seq("20151231", "avond", "consult"),
    Seq("20151231", "avond", "visite"),
    Seq("20160101", "nacht"),
    Seq("20160101", "ochtend", "consult"),
    Seq("20160101", "ochtend", "visite"),
    Seq("20160101", "avond", "consult"),
    Seq("20160101", "avond", "visite"),
    Seq("20160102", "nacht"),
    Seq("20160102", "ochtend", "consult"),
    Seq("20160102", "ochtend", "visite"),
    Seq("20160102", "avond", "consult"),
    Seq("20160102", "avond", "visite"),
    Seq("20160103", "nacht"),
    Seq("20160103", "ochtend", "consult"),
    Seq("20160103", "ochtend", "visite"),
    Seq("20160103", "avond", "consult"),
    Seq("20160103", "avond", "visite"),

    Seq("20160106", "avond", "visite"),
    Seq("20160108", "avond", "visite"),
    Seq("20160108", "avond", "consult"),
    Seq("20160109", "ochtend", "consult"),
    Seq("20160109", "ochtend", "visite"),
    Seq("20160110", "avond", "visite"),

    Seq("20160111", "avond", "visite"),
    Seq("20160113", "avond", "consult"),
    Seq("20160115", "avond", "consult"),
    Seq("20160117", "ochtend", "consult"),
    Seq("20160117", "avond", "visite"),

    Seq("20160118", "avond", "visite"),
    Seq("20160120", "avond", "consult"),
    Seq("20160121", "avond", "visite"),
    Seq("20160123", "avond", "consult"),
    Seq("20160124", "ochtend", "visite"),

    Seq("20160126", "avond", "visite"),
    Seq("20160127", "avond", "consult"),
    Seq("20160128", "avond", "consult"),
    Seq("20160130", "avond", "consult"),
    Seq("20160131", "ochtend", "consult"),

    Seq("20160203", "avond", "consult"),
    Seq("20160204", "avond", "visite"),
    Seq("20160205", "avond", "visite"),
    Seq("20160206", "ochtend", "visite"),
    Seq("20160207", "avond", "visite"),
    Seq("20160207", "avond", "consult"),

    Seq("20160208", "avond", "visite"),
    Seq("20160209", "avond", "consult"),
    Seq("20160213", "avond", "visite"),
    Seq("20160214", "ochtend", "visite"),

    Seq("20160217", "avond", "visite"),
    Seq("20160218", "avond", "consult"),
    Seq("20160219", "avond", "consult"),
    Seq("20160220", "ochtend", "consult"),
    Seq("20160220", "avond", "consult"),
    Seq("20160221", "ochtend", "consult"),

    Seq("20160223", "avond", "visite"),
    Seq("20160224", "avond", "consult"),
    Seq("20160225", "avond", "visite"),
    Seq("20160227", "ochtend", "consult"),
    Seq("20160228", "avond", "visite"),

    Seq("20160302", "avond", "consult"),
    Seq("20160303", "avond", "visite"),
    Seq("20160304", "avond", "consult"),
    Seq("20160305", "ochtend", "consult"),
    Seq("20160305", "avond", "visite"),

    Seq("20160307", "avond", "visite"),
    Seq("20160308", "avond", "consult"),
    Seq("20160312", "ochtend", "visite"),
    Seq("20160312", "avond", "visite"),
    Seq("20160313", "ochtend", "visite"),

    Seq("20160316", "avond", "visite"),
    Seq("20160317", "avond", "consult"),
    Seq("20160318", "avond", "visite"),
    Seq("20160320", "ochtend", "consult"),
    Seq("20160320", "avond", "consult"),

    Seq("20160321", "avond", "consult"),
    Seq("20160322", "avond", "visite"),
    Seq("20160326", "ochtend", "visite"),
    Seq("20160327", "ochtend", "visite"),
    Seq("20160327", "avond", "consult"),
    Seq("20160328", "ochtend", "consult"),

    Seq("20160330", "avond", "consult"),
    Seq("20160331", "avond", "visite"),
    Seq("20160401", "avond", "visite"),
    Seq("20160402", "avond", "consult"),
    Seq("20160403", "ochtend", "consult"),

    Seq("20160405", "avond", "visite"),
    Seq("20160406", "avond", "consult"),
    Seq("20160409", "avond", "visite"),
    Seq("20160410", "ochtend", "consult"),
    Seq("20160410", "avond", "consult"),

    Seq("20160411", "avond", "visite"),
    Seq("20160414", "avond", "consult"),
    Seq("20160415", "avond", "visite"),
    Seq("20160416", "ochtend", "consult"),
    Seq("20160417", "avond", "visite"),

    Seq("20160419", "avond", "consult"),
    Seq("20160421", "avond", "consult"),
    Seq("20160422", "avond", "consult"),
    Seq("20160423", "ochtend", "visite"),
    Seq("20160424", "ochtend", "consult"),

    Seq("20160426", "avond", "consult"),
    Seq("20160427", "ochtend", "consult"),
    Seq("20160428", "avond", "visite"),
    Seq("20160430", "ochtend", "consult"),
    Seq("20160430", "avond", "visite"),

    Seq("20160501", "ochtend", "visite"),
    Seq("20160504", "avond", "visite"),
    Seq("20160505", "avond", "consult"),
    Seq("20160506", "avond", "consult"),
    Seq("20160507", "avond", "visite"),
    Seq("20160508", "avond", "consult"),

    Seq("20160509", "avond", "consult"),
    Seq("20160511", "avond", "consult"),
    Seq("20160513", "avond", "visite"),
    Seq("20160514", "ochtend", "visite"),
    Seq("20160515", "ochtend", "consult"),
    Seq("20160516", "avond", "consult"),

    Seq("20160518", "avond", "visite"),
    Seq("20160519", "avond", "consult"),
    Seq("20160521", "ochtend", "visite"),
    Seq("20160521", "avond", "visite"),
    Seq("20160522", "avond", "consult"),

    Seq("20160524", "avond", "consult"),
    Seq("20160526", "avond", "visite"),
    Seq("20160528", "ochtend", "consult"),
    Seq("20160529", "ochtend", "visite"),
    Seq("20160531", "avond", "consult"),

    Seq("20160601", "avond", "visite"),
    Seq("20160602", "avond", "consult"),
    Seq("20160604", "ochtend", "consult"),
    Seq("20160604", "ochtend", "visite"),
    Seq("20160604", "avond", "visite"),
    Seq("20160604", "avond", "consult"),

    Seq("20160607", "avond", "visite"),
    Seq("20160610", "avond", "consult"),
    Seq("20160611", "avond", "visite"),
    Seq("20160612", "avond", "consult"),

    Seq("20160615", "avond", "visite"),
    Seq("20160616", "avond", "consult"),
    Seq("20160618", "avond", "consult"),
    Seq("20160619", "ochtend", "consult"),

    Seq("20160620", "avond", "visite"),
    Seq("20160621", "avond", "consult"),
    Seq("20160624", "avond", "visite"),
    Seq("20160625", "ochtend", "visite"),
    Seq("20160626", "ochtend", "visite"),
    Seq("20160626", "avond", "consult"),

    Seq("20160629", "avond", "consult"),
    Seq("20160630", "avond", "visite"),
    Seq("20160701", "avond", "visite"),
    Seq("20160702", "avond", "consult"),

    Seq("20160704", "avond", "visite"),
    Seq("20160705", "avond", "consult"),
    Seq("20160708", "avond", "consult"),
    Seq("20160709", "ochtend", "visite"),
    Seq("20160709", "avond", "visite"),

    Seq("20160712", "avond", "consult"),
    Seq("20160713", "avond", "visite"),
    Seq("20160714", "avond", "consult"),
    Seq("20160716", "ochtend", "consult"),
    Seq("20160717", "avond", "visite"),

    Seq("20160719", "avond", "visite"),
    Seq("20160722", "avond", "consult"),
    Seq("20160723", "avond", "consult"),
    Seq("20160724", "ochtend", "visite"),

    Seq("20160726", "avond", "visite"),
    Seq("20160727", "avond", "consult"),
    Seq("20160728", "avond", "visite"),
    Seq("20160730", "avond", "visite"),
    Seq("20160731", "ochtend", "consult"),

    Seq("20160801", "avond", "visite"),
    Seq("20160802", "avond", "consult"),
    Seq("20160805", "avond", "visite"),
    Seq("20160806", "ochtend", "visite"),
    Seq("20160807", "avond", "consult"),
    Seq("20160808", "avond", "consult"),
    Seq("20160810", "avond", "visite"),
    Seq("20160811", "avond", "consult"),
    Seq("20160813", "ochtend", "visite"),
    Seq("20160813", "ochtend", "consult"),
    Seq("20160814", "avond", "visite"),

    Seq("20160815", "avond", "consult"),
    Seq("20160817", "avond", "visite"),
    Seq("20160819", "avond", "visite"),
    Seq("20160821", "ochtend", "visite"),
    Seq("20160821", "avond", "consult"),

    Seq("20160824", "avond", "consult"),
    Seq("20160825", "avond", "visite"),
    Seq("20160828", "ochtend", "consult"),
    Seq("20160828", "avond", "consult"),

    Seq("20160830", "avond", "visite"),
    Seq("20160901", "avond", "visite"),
    Seq("20160902", "avond", "consult"),
    Seq("20160903", "avond", "visite"),
    Seq("20160904", "ochtend", "visite"),

    Seq("20160907", "avond", "consult"),
    Seq("20160908", "avond", "visite"),
    Seq("20160910", "ochtend", "consult"),
    Seq("20160911", "avond", "consult"),

    Seq("20160912", "avond", "visite"),
    Seq("20160913", "avond", "consult"),
    Seq("20160916", "avond", "visite"),
    Seq("20160917", "avond", "consult"),
    Seq("20160918", "ochtend", "consult"),

    Seq("20160920", "avond", "visite"),
    Seq("20160922", "avond", "consult"),
    Seq("20160923", "avond", "consult"),
    Seq("20160924", "ochtend", "visite"),
    Seq("20160925", "avond", "visite"),

    Seq("20160926", "avond", "consult"),
    Seq("20160928", "avond", "visite"),
    Seq("20160929", "avond", "consult"),
    Seq("20161001", "avond", "visite"),
    Seq("20161002", "ochtend", "consult"),

    Seq("20161005", "avond", "consult"),
    Seq("20161006", "avond", "visite"),
    Seq("20161007", "avond", "visite"),
    Seq("20161009", "ochtend", "visite"),
    Seq("20161009", "avond", "visite"),

    Seq("20161011", "avond", "visite"),
    Seq("20161012", "avond", "consult"),
    Seq("20161015", "avond", "consult"),
    Seq("20161016", "avond", "visite"),

    Seq("20161019", "avond", "visite"),
    Seq("20161020", "avond", "consult"),
    Seq("20161021", "avond", "visite"),
    Seq("20161022", "avond", "consult"),
    Seq("20161023", "ochtend", "visite"),

    Seq("20161025", "avond", "consult"),
    Seq("20161026", "avond", "visite"),
    Seq("20161027", "avond", "consult"),
    Seq("20161029", "avond", "visite"),
    Seq("20161030", "ochtend", "consult"),

    Seq("20161101", "avond", "consult"),
    Seq("20161102", "avond", "consult"),
    Seq("20161103", "avond", "visite"),
    Seq("20161106", "ochtend", "visite"),
    Seq("20161106", "avond", "visite"),

    Seq("20161107", "avond", "visite"),
    Seq("20161108", "avond", "consult"),
    Seq("20161111", "avond", "consult"),
    Seq("20161112", "ochtend", "consult"),
    Seq("20161113", "avond", "consult"),

    Seq("20161116", "avond", "visite"),
    Seq("20161117", "avond", "consult"),
    Seq("20161118", "avond", "visite"),
    Seq("20161119", "avond", "consult"),
    Seq("20161120", "ochtend", "visite"),

    Seq("20161121", "avond", "consult"),
    Seq("20161122", "avond", "visite"),
    Seq("20161126", "avond", "consult"),
    Seq("20161127", "ochtend", "consult"),

    Seq("20161128", "avond", "visite"),
    Seq("20161130", "avond", "consult"),
    Seq("20161201", "avond", "visite"),
    Seq("20161202", "avond", "consult"),
    Seq("20161203", "ochtend", "visite"),
    Seq("20161203", "avond", "visite"),

    Seq("20161206", "avond", "consult"),
    Seq("20161207", "avond", "visite"),
    Seq("20161210", "ochtend", "consult"),
    Seq("20161211", "avond", "consult"),

    Seq("20161214", "avond", "visite"),
    Seq("20161215", "avond", "consult"),
    Seq("20161216", "avond", "visite"),
    Seq("20161217", "ochtend", "visite"),
    Seq("20161218", "avond", "visite"),

    Seq("20161219", "avond", "consult"),
    Seq("20161220", "avond", "visite"),
    Seq("20161222", "avond", "consult"),
    Seq("20161225", "ochtend", "visite"),
    Seq("20161225", "avond", "consult"),

    Seq("20161226", "avond", "visite"),
    Seq("20161229", "avond", "visite"),
    Seq("20161231", "ochtend", "visite"),
    Seq("20161231", "avond", "consult"),
    Seq("20170101", "ochtend", "visite"),

    Seq("20170103", "avond", "visite"),
    Seq("20170105", "avond", "consult"),
    Seq("20170106", "avond", "consult"),
    Seq("20170107", "ochtend", "visite"),
    Seq("20170107", "avond", "consult"),
    Seq("20170108", "ochtend", "consult")

  )

  val nightTasks = Seq("20160112", "20160113", "20160119", "20160120", "20160121", "20160122", "20160123", "20160128", "20160129", "20160130",
    "20160207", "20160222", "20160223", "20160224", "20160225", "20160226", "20160317", "20160318", "20160319", "20160329", "20160330", "20160331",
    "20160414", "20160415", "20160416", "20160417", "20160426", "20160427", "20160428", "20160429", "20160430", "20160507", "20160508",
    "20160516", "20160517", "20160518", "20160521", "20160522", "20160529", "20160530", "20160602", "20160603", "20160615", "20160616", "20160617",
    "20160622", "20160623", "20160708", "20160709", "20160716", "20160717", "20160718", "20160723", "20160724", "20160809", "20160810", "20160811",
    "20160818", "20160819", "20160820", "20160824", "20160825", "20160901", "20160902", "20160911", "20160912", "20160913", "20160920",
    "20160921", "20160922", "20160926", "20160927", "20160928", "20161007", "20161008", "20161009", "20161015", "20161016", "20161020",
    "20161021", "20161027", "20161028", "20161113", "20161114", "20161122", "20161123", "20161124", "20161125", "20161126", "20161213",
    "20161214", "20161222", "20161223", "20161229", "20161230", "20161231")

  val weekDaysWithoutFriday = WeekDaySelection(monday to thursday)
  val weekDays = WeekDaySelection(monday to friday)
  val weekendDaysIncludingFriday = WeekDaySelection(friday to sunday)
  val weekendDays = WeekDaySelection(saturday to sunday)

  val holidaySelection = DayIdSelection(holidays.collect { case (id, label, _) ⇒ id }.toSeq)
  val holidayWholeDaySelection = DayIdSelection(holidays.collect { case (id, label, true) ⇒ id }.toSeq)
  val holidayPartDaySelection = DayIdSelection(holidays.collect { case (id, label, false) ⇒ id }.toSeq)
  val noHolidaySelection = InverseSelection(holidaySelection)
  val noWholeHolidaySelection = InverseSelection(holidayWholeDaySelection)
  val holidayLabels = holidays.map { case (id, label, _) ⇒ (id, label) }.toMap

  def create(calendars: ActorRef)(implicit timeout: Timeout, executionContext: ExecutionContext) = {
    (calendars ? CreateCalendar("2016", DateTime("2015-12-28"), DateTime("2017-01-08"), holidayLabels)).onComplete {
      case Success(CalendarCreated(_)) ⇒ {
        (calendars ? GetCalendar("2016")).onComplete {
          case Success(GotCalendar(calendar)) ⇒
            calendar ! CreateTasks(label = "nacht", start = 0 :: 0, end = 8 :: 0, Set("week", "nacht"), Seq(weekDays, noWholeHolidaySelection))
            calendar ! CreateTasks("visite", start = 17 :: 0, end = 23 :: 0, Set("week", "visite", "avond"), Seq(weekDaysWithoutFriday, noHolidaySelection))
            calendar ! CreateTasks("consult", start = 17 :: 0, end = 23 :: 0, Set("week", "consult", "avond"), Seq(weekDaysWithoutFriday, noHolidaySelection))
            calendar ! CreateTasks("consult", start = 17 :: 0, end = 23 :: 0, Set("weekend", "consult", "avond"), Seq(WeekDaySelection(Seq(friday)), noHolidaySelection))
            calendar ! CreateTasks("visite", start = 17 :: 0, end = 23 :: 0, Set("weekend", "visite", "avond"), Seq(WeekDaySelection(Seq(friday)), noHolidaySelection))
            calendar ! CreateTasks("nacht", start = 0 :: 0, end = 8 :: 0, Set("weekend", "nacht"), Seq(weekendDays, noWholeHolidaySelection))
            calendar ! CreateTasks("consult", start = 8 :: 0, end = 16 :: 0, Set("weekend", "consult", "ochtend"), Seq(weekendDays, noWholeHolidaySelection))
            calendar ! CreateTasks("consult", start = 16 :: 0, end = 23 :: 0, Set("weekend", "consult", "avond"), Seq(weekendDays, noHolidaySelection))
            calendar ! CreateTasks("visite", start = 9 :: 0, end = 16 :: 0, Set("weekend", "visite", "ochtend"), Seq(WeekDaySelection(Seq(saturday)), noWholeHolidaySelection))
            calendar ! CreateTasks("visite", start = 9 :: 0, end = 16 :: 0, Set("weekend", "visite", "ochtend"), Seq(WeekDaySelection(Seq(sunday)), noWholeHolidaySelection))
            calendar ! CreateTasks("visite", start = 16 :: 0, end = 23 :: 0, Set("weekend", "visite", "avond"), Seq(WeekDaySelection(Seq(saturday)), noHolidaySelection))
            calendar ! CreateTasks("visite", start = 16 :: 0, end = 23 :: 0, Set("weekend", "visite", "avond"), Seq(WeekDaySelection(Seq(sunday)), noHolidaySelection))

            calendar ! CreateTasks("nacht", start = 0 :: 0, end = 8 :: 0, Set("weekend", "nacht", "feest"), Seq(holidayWholeDaySelection))
            calendar ! CreateTasks("visite", start = 9 :: 0, end = 16 :: 0, Set("weekend", "visite", "ochtend", "feest"), Seq(holidayWholeDaySelection))
            calendar ! CreateTasks("consult", start = 8 :: 0, end = 16 :: 0, Set("weekend", "consult", "ochtend", "feest"), Seq(holidayWholeDaySelection))
            calendar ! CreateTasks("visite", start = 16 :: 0, end = 23 :: 0, Set("weekend", "visite", "avond", "feest"), Seq(holidaySelection))
            calendar ! CreateTasks("consult", start = 16 :: 0, end = 23 :: 0, Set("weekend", "consult", "avond", "feest"), Seq(holidaySelection))

            resources.map { resource ⇒
              calendar ! AddResource(resource)
            }

            counters.map { counter ⇒
              calendar ! AddCounter(counter)
            }

            calendar ! CreateSchedule("Opzet", Map.empty, resourceConstraints)

            ignoreTasks.foreach { item ⇒
              calendar ! AddTags(Set("ignore"), Seq(TaskOnDaySelection(item.head), IncludeTags(item.tail)))
            }

            calendar ! AddTags(Set("ignore"), Seq(TaskNotOnDaySelection(nightTasks), IncludeTags(Seq("nacht"))))

          case Success(_) ⇒
          case Failure(_) ⇒ println("foutje")
        }
      }
      case Success(CalendarDidAlreadyExist) ⇒ println("calndar did already exist")
      case Success(_)                       ⇒ println("Unexpected success")
      case Failure(ex)                      ⇒ println(s"error: $ex")
    }
  }
}
