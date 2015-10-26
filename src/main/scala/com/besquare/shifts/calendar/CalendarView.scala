package com.besquare
package shifts
package calendar

import scala.annotation.tailrec

case class CalendarView(name: String, from: DateTime, to: DateTime, children: Seq[Year]) extends CalendarNode[Year] {
  def years = children
  def months = years.foldLeft(Seq[Month]()) {
    case (months, year) ⇒ months ++ year.months
  }
  def weeks = months.foldLeft(Seq[PartialWeek]()) {
    case (weeks, month) ⇒ weeks ++ month.weeks
  }
  def filter(filters: Seq[DaySelection]) = filters.foldLeft(days) {
    case (days, daySelection) ⇒ days.filter(daySelection.exec)
  }
}

object CalendarView {
  def Empty = CalendarView("", DateTime("2000-01-01"), DateTime("2000-01-01"), Seq.empty)

  def apply(name: String, from: DateTime, to: DateTime, dayLabels: Map[DayId, String]) = {
    @tailrec
    def daysMapping(date: DateTime, mapping: Map[YearNumber, Map[MonthNumber, Map[WeekNumber, Seq[(DayNumber, DayOfWeekNumber)]]]] = Map.empty): Map[YearNumber, Map[MonthNumber, Map[WeekNumber, Seq[(DayNumber, DayOfWeekNumber)]]]] = {
      val year: YearNumber = date.getYear
      val month: MonthNumber = date.getMonthOfYear
      val week: WeekNumber = date.getWeekOfWeekyear
      val day: DayNumber = date.getDayOfMonth
      val dayOfWeek: DayOfWeekNumber = date.getDayOfWeek
      if (date.isBefore(to) || date == to) {
        val updatedDays = mapping.getOrElse(year, Map.empty).getOrElse(month, Map.empty).getOrElse(week, Seq.empty) :+ (day, dayOfWeek)
        val updatedWeeks = mapping.getOrElse(year, Map.empty).getOrElse(month, Map.empty) ++ Map(week -> updatedDays)
        val updatedMonths = mapping.getOrElse(year, Map.empty) ++ Map(month -> updatedWeeks)
        daysMapping(date.plusDays(1), mapping ++ Map(year -> updatedMonths))
      } else mapping
    }

    def sortWeeks(weeks: Iterable[WeekNumber], month: MonthNumber): List[WeekNumber] = {
      val sorted = weeks.toList.sorted
      sorted.reverse match {
        case w :: _ if month == 1 && (w >= 52) ⇒ w :: sorted.take(sorted.length - 1)
        case _                                 ⇒ sorted
      }
    }

    val calendar = daysMapping(from)

    lazy val calendarView = new CalendarView(name, from, to, years)
    lazy val years: Seq[Year] = calendar.keys.toList.sorted.map { yearNumber ⇒
      lazy val year = Year(yearNumber, () ⇒ calendarView, months)
      lazy val months: Seq[Month] = calendar(yearNumber).keys.toList.sorted.map { monthNumber ⇒
        lazy val month = Month(monthNumber, () ⇒ year, weeks)
        lazy val weeks: Seq[PartialWeek] = sortWeeks(calendar(yearNumber)(monthNumber).keys, monthNumber).map { weekNumber ⇒
          lazy val week = PartialWeek(weekNumber, () ⇒ month, days)
          lazy val days: Seq[Day] = calendar(yearNumber)(monthNumber)(weekNumber).map {
            case (day, dayOfWeek) ⇒
              val label = dayLabels.getOrElse(Day.id(yearNumber, monthNumber, day), "")
              Day(label, day, dayOfWeek, () ⇒ week)
          }
          week
        }
        month
      }
      year
    }

    calendarView
  }
}
