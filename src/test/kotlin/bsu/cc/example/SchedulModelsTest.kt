package bsu.cc.example

import bsu.cc.schedule.ClassSchedule
import bsu.cc.schedule.DateInterval
import bsu.cc.schedule.Instructor
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class SchedulModelsTest : WordSpec() {
    init {
        "Kotlin" should {
            "stop warning me about unused parameters" {
                ClassSchedule(
                        startTime = LocalTime.of(2, 30),
                        endTime = LocalTime.of(3, 45),
                        meetingDays = setOf(DayOfWeek.MONDAY),
                        meetingDates = DateInterval(
                                LocalDate.ofEpochDay(1),
                                LocalDate.ofEpochDay(2)),
                        subject = "ECE",
                        catalogNumber = "330",
                        section = "1",
                        room = "110",
                        instructors = setOf(Instructor("Mark", "Anderson")),
                        description = "Literally the best class"
                ).shouldNotBe(null)
            }
        }
    }
}
