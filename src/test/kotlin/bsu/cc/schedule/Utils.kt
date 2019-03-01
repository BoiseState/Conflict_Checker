package bsu.cc.schedule

import bsu.cc.constraints.ClassConstraint
import bsu.cc.constraints.ConstraintPriority
import bsu.cc.constraints.createPriorityFrom
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * list equality depends on position, so sort sub-lists first
 * map returns list, so convert back to set
 * sets equality does not depend on position
 */
fun checkOverlapsAreEqual(actual: Set<List<ClassSchedule>>, expected: Set<List<ClassSchedule>>): Boolean
 = actual.map { it.sorted() }.toSet() == expected.map { it.sorted() }.toSet()


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

fun timeFrom(str: String): LocalTime {
    val split = str.split(":")
    return LocalTime.of(split[0].toInt(), split[1].toInt())
}

fun createDummyClass(subject: String, catalogNumber: String, section: String,
                     start: String = "1:00", end: String = "2:00")
        = ClassSchedule(
        startTime = timeFrom(start),
        endTime = timeFrom(end),
        meetingDays = setOf(DayOfWeek.MONDAY),
        meetingDates = DateInterval(
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2)),
        subject = subject,
        catalogNumber = catalogNumber,
        section = section,
        room = "123",
        instructors = setOf(Instructor("Test", "Joe")),
        description = "dummy"
)

fun createContraint(id: Int, priority: ConstraintPriority, vararg classes: String)
    = ClassConstraint(id, priority, classes.toSet())
