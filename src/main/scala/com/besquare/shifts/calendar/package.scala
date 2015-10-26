package com.besquare
package shifts

package object calendar {
  //import com.github.nscala_time.time.Imports._

  type DateTime = com.github.nscala_time.time.Imports.DateTime

  type YearNumber = Int
  type MonthNumber = Int
  type WeekNumber = Int
  type DayOfWeekNumber = Int
  type DayNumber = Int
  type DayId = String

  val monday: DayOfWeekNumber = 1
  val tuesday: DayOfWeekNumber = 2
  val wednesday: DayOfWeekNumber = 3
  val thursday: DayOfWeekNumber = 4
  val friday: DayOfWeekNumber = 5
  val saturday: DayOfWeekNumber = 6
  val sunday: DayOfWeekNumber = 7

  implicit class SeqHelper[T](e: T) {
    def in(seq: Seq[T]) = seq.contains(e)
  }

  case class Year(number: YearNumber, parent: () ⇒ CalendarView, children: Seq[Month]) extends CalendarNode[Month]
      with NeighbourSupport[Year, CalendarView] {
    def months = children
    override lazy val toString = s"${number}"
  }

  case class Month(number: MonthNumber, parent: () ⇒ Year, children: Seq[PartialWeek]) extends CalendarNode[PartialWeek]
      with NeighbourSupport[Month, Year] {
    def weeks = children
    def year = parent()
    def name = Month.longNames(number)
    override lazy val toString = Month.shortNames(number) + s", ${year.number}"
  }

  case object Month {
    val longNames = Seq("_", "Januari", "Februari", "Maart", "April", "Mei", "Juni", "Juli", "Augustus", "September", "Oktober", "November", "December")
    val shortNames = Seq("_", "Jan", "Feb", "Mrt", "Apr", "Mei", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec")
  }

  case class PartialWeek(number: WeekNumber, parent: () ⇒ Month, children: Seq[Day]) extends CalendarNode[Day]
      with NeighbourSupport[PartialWeek, Month] {
    def month = parent()
    def year: Year = previous.fold(month.year) {
      case week if week.number == number && month.number == 1 ⇒ month.year.previous.get
      case _                                                  ⇒ month.year
    }

    def id = s"${year.number}-$number"

    override lazy val toString = s"Week ${number}, ${year.number}"
  }

  case class Week(number: WeekNumber, year: YearNumber, days: Seq[Day]) {
    def id = s"$year-$number"
  }

  case object Week {
    def apply(partialWeeks: Seq[PartialWeek]): Week = {
      require(partialWeeks.size > 0 && partialWeeks.map(_.id).toSet.size == 1)
      val days = partialWeeks.foldLeft(Seq[Day]()) {
        case (days, partialWeek) ⇒ days ++ partialWeek.days
      }
      Week(partialWeeks.head.number, partialWeeks.head.year.number, days)
    }
  }

  case object Day {
    val longDayOfWeekNames = Seq("_", "Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag", "Zondag")
    val shortDayOfWeekNames = Seq("_", "Ma", "Di", "Wo", "Do", "Vr", "Za", "Zo")
    def id(year: YearNumber, month: MonthNumber, day: DayNumber) = f"${year}${month}%02d${day}%02d"
  }

  case object DateTime {
    def apply(year: Int, month: Int, day: Int = 0) = new DateTime(year, month, day, 0, 0, 0, 0)
    def apply(date: String) = new DateTime(date)
  }

  case class Day(label: String, number: DayNumber, dayOfWeek: DayOfWeekNumber, parent: () ⇒ PartialWeek) extends NeighbourSupport[Day, PartialWeek] {
    def week = parent()
    def month = week.month
    def year = month.year

    lazy val id: DayId = Day.id(year.number, month.number, number)

    def toDateTime = DateTime(year.number, month.number, number)

    override lazy val toString = id
  }

}
