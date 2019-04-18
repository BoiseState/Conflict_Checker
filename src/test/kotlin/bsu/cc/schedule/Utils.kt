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

// for testing date overlaps
fun createDummyClass(epoch1: Long, epoch2: Long, section: String = "1")
        = ClassSchedule(
        startTime = LocalTime.of(1, 0),
        endTime = LocalTime.of(2, 0),
        meetingDays = setOf(DayOfWeek.MONDAY),
        meetingDates = DateInterval(
                LocalDate.ofEpochDay(epoch1),
                LocalDate.ofEpochDay(epoch2)),
        subject = "ECE",
        catalogNumber = "330",
        section = section,
        room = "123",
        instructors = setOf(Instructor("Test", "Joe")),
        description = "dummy"
)

// for testing day overlaps
fun createDummyClass(day: DayOfWeek, section: String = "1")
        = ClassSchedule(
        startTime = LocalTime.of(1, 0),
        endTime = LocalTime.of(2, 0),
        meetingDays = setOf(day),
        meetingDates = DateInterval(
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2)),
        subject = "ECE",
        catalogNumber = "330",
        section = section,
        room = "123",
        instructors = setOf(Instructor("Test", "Joe")),
        description = "dummy"
)

fun timeFrom(str: String): LocalTime {
    val split = str.split(":")
    return LocalTime.of(split[0].toInt(), split[1].toInt())
}

fun createDummyClass(section: String, start: String, end: String, vararg instructors: Instructor)
        = ClassSchedule(
        startTime = timeFrom(start),
        endTime = timeFrom(end),
        meetingDays = setOf(DayOfWeek.MONDAY),
        meetingDates = DateInterval(
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2)),
        subject = "cs",
        catalogNumber = "121",
        section = section,
        room = "123",
        instructors = instructors.toSet(),
        description = "dummy"
)

fun createDummyClass(subject: String, catalogNumber: String, section: String,
                     start: String = "1:00", end: String = "2:00", room: String = "123")
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
        room = room,
        instructors = setOf(Instructor("Test", "Joe")),
        description = "dummy"
)

fun createConstraint(id: Int, priority: ConstraintPriority, vararg classes: String)
    = ClassConstraint(id, priority, classes.toSet())
