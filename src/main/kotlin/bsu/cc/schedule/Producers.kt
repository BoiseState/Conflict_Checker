package bsu.cc.schedule

import bsu.cc.parser.getFromCellOrDefault
import bsu.cc.parser.getFromCellOrThrow
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet
import java.lang.IllegalArgumentException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val CLASS_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
val MEETING_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

fun classScheduleProducer(rowMap: Map<String, Cell>): ClassSchedule {
    val meetingRangeSplit = getFromCellOrThrow<String>(rowMap["Meeting Time/Days"]).trim().toUpperCase().split("-")
    val startTimeString = meetingRangeSplit[0]
    val endTimeString = meetingRangeSplit[1].dropLastWhile { it != ' '}.dropLast(1)
    val daysString = meetingRangeSplit[1].takeLastWhile { it != ' ' }
    val meetingDatesString = getFromCellOrThrow<String>(rowMap["Meeting Dates"]).trim()
    val startDateString = meetingDatesString.takeWhile { it != '-' }
    val endDateString = meetingDatesString.takeLastWhile { it != '-' }
    val subject = getFromCellOrThrow<String>(rowMap["Subject"])
    val catalogNumber = getFromCellOrThrow<Number>(rowMap["Catalog Nbr"])
    val section = getFromCellOrThrow<Number>(rowMap["Class Section"])
    val room = getFromCellOrDefault(rowMap["Room"], "")
    val instructors = stringToInstructorSet(getFromCellOrThrow(rowMap["Instructors"]))
    val description = getFromCellOrThrow<String>(rowMap["Descr"])

    return ClassSchedule(
        startTime = LocalTime.parse(startTimeString, CLASS_TIME_FORMATTER),
        endTime = LocalTime.parse(endTimeString, CLASS_TIME_FORMATTER),
        meetingDays = daysString.chunked(2).map { stringToDayOfWeek(it) }.toSet(),
        meetingDates = DateInterval(
                LocalDate.parse(startDateString, MEETING_DATE_FORMATTER),
                LocalDate.parse(endDateString, MEETING_DATE_FORMATTER)
        ),
        subject = subject,
        catalogNumber = catalogNumber.toInt().toString(),
        section = section.toInt().toString(),
        room = room,
        instructors = instructors,
        description = description
    )
}

fun classScheduleToRow(classSchedule: ClassSchedule, sheet: Sheet, rowIndex: Int) {
    val row = sheet.createRow(rowIndex)
    row.createCell(0).setCellValue(classSchedule.subject)
    row.createCell(1).setCellValue(classSchedule.catalogNumber.toDouble())
    row.createCell(2).setCellValue(classSchedule.description)
    row.createCell(3).setCellValue(classSchedule.section.toDouble())
    row.createCell(4).setCellValue(meetingDatesToString(classSchedule.meetingDates))
    row.createCell(5).setCellValue(meetingTimeDaysToString(classSchedule.startTime, classSchedule.endTime, classSchedule.meetingDays))
    row.createCell(6).setCellValue(classSchedule.room)
    row.createCell(7).setCellValue(classSchedule.instructors.joinToString { "${it.lastName},${it.firstName}, " })
}

private fun meetingDatesToString(meetingDates: DateInterval): String {
    return "${meetingDates.startDate.format(MEETING_DATE_FORMATTER)}-${meetingDates.endDate.format(MEETING_DATE_FORMATTER)}"
}

private fun meetingTimeDaysToString(startTime: LocalTime, endTime: LocalTime, meetingDays: Set<DayOfWeek>): String {
    return "${startTime.format(CLASS_TIME_FORMATTER)}-${endTime.format(CLASS_TIME_FORMATTER)} ${meetingDays.joinToString(""){ dayOfWeekToAbbr(it) }}"
}

private fun stringToInstructorSet(input: String): Set<Instructor> {
    val instructorStrings = input.split(", ")
    return instructorStrings.map{ it.trim() }.map { s ->
        Instructor(s.takeLastWhile { it != ',' } , s.takeWhile { it != ',' } )
    }.toSet()
}

private fun stringToDayOfWeek(dayAbbreviation: String): DayOfWeek {
    return when(dayAbbreviation.toLowerCase()) {
        "su" -> DayOfWeek.SUNDAY
        "mo" -> DayOfWeek.MONDAY
        "tu" -> DayOfWeek.TUESDAY
        "we" -> DayOfWeek.WEDNESDAY
        "th" -> DayOfWeek.THURSDAY
        "fr" -> DayOfWeek.FRIDAY
        "sa" -> DayOfWeek.SATURDAY
        else -> throw IllegalArgumentException("Invalid day abbreviation")
    }
}

private fun dayOfWeekToAbbr(dayOfWeek: DayOfWeek): String {
    return when(dayOfWeek) {
        DayOfWeek.SUNDAY -> "Su"
        DayOfWeek.MONDAY -> "Mo"
        DayOfWeek.TUESDAY -> "Tu"
        DayOfWeek.WEDNESDAY -> "We"
        DayOfWeek.THURSDAY -> "Th"
        DayOfWeek.FRIDAY -> "Fr"
        DayOfWeek.SATURDAY -> "Sa"
    }
}