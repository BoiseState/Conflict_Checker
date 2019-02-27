package bsu.cc.schedule

import com.brein.time.timeintervals.intervals.IInterval
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime


data class Instructor(val firstName: String, val lastName: String)

data class DateInterval(val startDate: LocalDate, val endDate: LocalDate)

class ClassSchedule(val startTime: LocalTime,
                    val endTime: LocalTime,
                    val meetingDays: Set<DayOfWeek>,
                    val meetingDates: DateInterval,
                    val subject: String,
                    val catalogNumber: String,
                    val section: String,
                    val room: String,
                    val instructors: Set<Instructor>,
                    val description: String
                    ) : IInterval<Int> {

    companion object {
        val xlsxHeaders = listOf("Subject", "Catalog Nbr", "Descr", "Class Section", "Meeting Dates", "Meeting Time/Days", "Room", "Instructors")
    }

    override fun compareTo(other: IInterval<*>?): Int {
        val cmpStart = startTime.toSecondOfDay() - other?.getNormStart() as Int
        if (cmpStart == 0) {
            return endTime.toSecondOfDay() - other.getNormEnd() as Int
        }
        return cmpStart
    }

    override fun getUniqueIdentifier(): String = "[$startTime, $endTime]"

    override fun getNormStart(): Int = startTime.toSecondOfDay()

    override fun getNormEnd(): Int = endTime.toSecondOfDay()

    // auto generated
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassSchedule

        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (meetingDays != other.meetingDays) return false
        if (meetingDates != other.meetingDates) return false
        if (subject != other.subject) return false
        if (catalogNumber != other.catalogNumber) return false
        if (section != other.section) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + meetingDays.hashCode()
        result = 31 * result + meetingDates.hashCode()
        result = 31 * result + subject.hashCode()
        result = 31 * result + catalogNumber.hashCode()
        result = 31 * result + section.hashCode()
        return result
    }

    override fun toString(): String {
        return "[$subject$catalogNumber-$section  $startTime, $endTime]"
    }

}

