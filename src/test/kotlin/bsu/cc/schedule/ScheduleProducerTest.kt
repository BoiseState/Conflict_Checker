package bsu.cc.schedule

import bsu.cc.parser.readWorkbook
import bsu.cc.parser.sheetToDataClasses
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class ScheduleProducerTest : WordSpec() {
    private val basePath = "src/test/resources/xlsx/"

    init {
        "Schedule producers" should {

            val happyPathPath = basePath + "ProducerTestValid.xlsx"
            val invalidFileFormatPath = basePath + "UnevenRowLengths.xlsx"
            val createdFilePath = basePath + "ProdcerCreatedFile.xlsx"
            val dateFormat = SimpleDateFormat("MM/dd/yyyy")

            fun defaultParseWithProducer(filePath: String): List<ClassSchedule> {
                val sheet = readWorkbook(filePath).getSheetAt(0)
                return sheetToDataClasses(
                        sheet = sheet,
                        dataProducer = ::classScheduleProducer,
                        ignoreDuplicateHeaders = true
                ).toList()
            }

            "Correctly map from a file" {
                defaultParseWithProducer(happyPathPath).zip(happyPathExpectedClasses).forEach{ (producedClass, expectedClass) ->
                    producedClass shouldBe expectedClass
                }
            }

            "Throw exception for invalid file format" {
                shouldThrow<IllegalArgumentException> {
                    defaultParseWithProducer(invalidFileFormatPath)
                }
            }

            "Correctly write a cleaned file" {
                FileOutputStream(createdFilePath).use {
                    val workbook = XSSFWorkbook()
                    val newSheet = workbook.createSheet()
                    val headerRow = newSheet.createRow(0)

                    ClassSchedule.xlsxHeaders.withIndex().forEach{ (index, header) ->
                        headerRow.createCell(index).setCellValue(header)
                    }
                    happyPathExpectedClasses.withIndex().forEach { (index, classSchedule) ->
                        classScheduleToRow(classSchedule, newSheet, index + 1)
                    }

                    workbook.write(it)
                }

                //This is not as rigorous as it could be, however what is important is that the read/write conversions are idempotent, which this guarantees
                defaultParseWithProducer(createdFilePath).zip(happyPathExpectedClasses).forEach{ (producedClass, expectedClass) ->
                    producedClass shouldBe expectedClass
                }
            }
        }
    }

    private val happyPathExpectedClasses = listOf(
        ClassSchedule(
                startTime = LocalTime.of(18, 0),
                endTime = LocalTime.of(19, 15),
                meetingDays = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
                meetingDates = DateInterval(
                        LocalDate.of(2019, 1, 14),
                        LocalDate.of(2019, 5, 3)),
                subject = "CS",
                catalogNumber = "111",
                section = "1",
                room = "CCP240",
                instructors = setOf(Instructor("Liljana", "Babinkostova"), Instructor("Grady", "Wright"), Instructor("Francesca", "Spezzano")),
                description = "Introduction to Programming"
        ),
        ClassSchedule(
                startTime = LocalTime.of(7, 30),
                endTime = LocalTime.of(8, 45),
                meetingDays = setOf(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                meetingDates = DateInterval(
                        LocalDate.of(2019, 2, 14),
                        LocalDate.of(2019, 5, 10)),
                subject = "CS",
                catalogNumber = "117",
                section = "1",
                room = "CCP221",
                instructors = setOf(Instructor("Jodi L", "Mead")),
                description = "C++ for Engineers"
        ),
        ClassSchedule(
                startTime = LocalTime.of(7, 30),
                endTime = LocalTime.of(8, 45),
                meetingDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                meetingDates = DateInterval(
                        LocalDate.of(2019, 1, 14),
                        LocalDate.of(2019, 5, 3)),
                subject = "DIFFCS",
                catalogNumber = "117",
                section = "2",
                room = "CCP240",
                instructors = setOf(Instructor("Cathie", "Olschanowsky")),
                description = "C++ for Engineers"
        )
    )
}