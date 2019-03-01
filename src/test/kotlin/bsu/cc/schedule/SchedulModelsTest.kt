package bsu.cc.schedule

import bsu.cc.constraints.ClassConstraint
import bsu.cc.constraints.ConstraintPriority
import io.kotlintest.matchers.boolean.shouldBeFalse
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

            "be comparable to constraints" {
                val constraint = ClassConstraint(
                        id = 1,
                        priority = ConstraintPriority.PRIORITY,
                        classes = setOf("cs121", "cs221", "ece230")
                )

                val hits = listOf(
                        createDummyClass("CS", "121", "1"),
                        createDummyClass("Cs", "221", "1"),
                        createDummyClass("ECe", "230", "1")
                )

                val misses = listOf(
                        createDummyClass("CS", "321", "1"),
                        createDummyClass("Cs", "421", "1"),
                        createDummyClass("ECe", "330", "1")
                )

                hits.forEach { constraint.classes.contains(it.classString).shouldBeTrue() }
                misses.forEach { constraint.classes.contains(it.classString).shouldBeFalse() }
            }
        }
    }
}

