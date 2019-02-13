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

}

