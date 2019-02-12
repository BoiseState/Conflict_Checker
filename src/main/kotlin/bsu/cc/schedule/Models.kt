package bsu.cc.schedule

import com.brein.time.timeintervals.intervals.IInterval
import com.brein.time.timeintervals.intervals.NumberInterval
import java.io.Serializable
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
                    ) : IInterval<Long> {


    override fun compareTo(other: IInterval<*>?): Int {
        val cmpStart = startTime.toNanoOfDay() - other?.getNormStart() as Long
        if (cmpStart == 0L) {
            val cmpEnd = endTime.toNanoOfDay() - other.getNormEnd() as Long
            return when {
                cmpEnd > 0 -> 1
                cmpEnd < 0 -> -1
                else -> 0
            }
        }
        return when {
            cmpStart > 0 -> 1
            cmpStart < 0 -> -1
            else -> 0
        }
    }

    override fun getUniqueIdentifier(): String = "[$startTime, $endTime]"


    override fun getNormStart(): Long = startTime.toNanoOfDay()

    override fun getNormEnd(): Long = endTime.toNanoOfDay()

}

