package bsu.cc.example

import bsu.cc.schedule.*
import com.brein.time.timeintervals.collections.ListIntervalCollection
import com.brein.time.timeintervals.filters.IntervalFilter
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder
import com.brein.time.timeintervals.indexes.IntervalValueComparator
import io.kotlintest.matchers.boolean.shouldBeTrue
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
        "Schedule" should {
            "work with our time-interval library" {

                val tree = buildScheduleIntervalTree()

                val classes = listOf(
                        createDummyClass(1, 1, 30, 2, 45),
                        createDummyClass(2, 2, 30, 3, 45),
                        createDummyClass(3, 4, 30, 4, 45),
                        createDummyClass(4, 4, 30, 4, 45),
                        createDummyClass(5, 5, 30, 5, 45)
                )
                classes.forEach { tree.add(it) }

                val overlap1_2 = listOf(classes[0], classes[1])
                val overlap3_4 = listOf(classes[2], classes[3])
                val alone = listOf(classes[4])

                //check overlaps
                tree.overlap(classes[0]).forEach { clazz ->
                    (clazz in overlap1_2).shouldBeTrue()
                }
                tree.overlap(classes[1]).forEach { clazz ->
                    (clazz in overlap1_2).shouldBeTrue()
                }
                tree.overlap(classes[2]).forEach { clazz ->
                    (clazz in overlap3_4).shouldBeTrue()
                }
                tree.overlap(classes[3]).forEach { clazz ->
                    (clazz in overlap3_4).shouldBeTrue()
                }
                tree.overlap(classes[4]).forEach { clazz ->
                    (clazz in alone).shouldBeTrue()
                }

                //check find
                tree.find(classes[2]).forEach { clazz ->
                    (clazz in overlap3_4).shouldBeTrue()
                }
                tree.find(classes[3]).forEach { clazz ->
                    (clazz in overlap3_4).shouldBeTrue()
                }
                tree.find(classes[4]).forEach { clazz ->
                    (clazz in alone).shouldBeTrue()
                }
            }
        }
    }
}

fun createDummyClass(index: Int, hour1: Int, min1: Int, hour2: Int, min2: Int): ClassSchedule
    = ClassSchedule(
        startTime = LocalTime.of(hour1, min1),
        endTime = LocalTime.of(hour2, min2),
        meetingDays = setOf(DayOfWeek.MONDAY),
        meetingDates = DateInterval(
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2)),
        subject = "ECE$index",
        catalogNumber = "330",
        section = "1",
        room = "123",
        instructors = setOf(Instructor("Test", "Joe")),
        description = "dummy"
)

