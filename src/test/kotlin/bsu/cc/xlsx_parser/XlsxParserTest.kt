package bsu.cc.xlsx_parser

import bsu.cc.constraints.readConstraintFile
import bsu.cc.data_classes.DemoDataClass
import bsu.cc.parser.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.apache.poi.ss.usermodel.Cell
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class XlsxParserTest : WordSpec() {
    private val basePath = "src/test/resources/xlsx/"

    init {
        "Parsing an XLSX" should {

            val happyPathPath = basePath + "HappyPath.xlsx"
            val duplicateHeadersPath = basePath + "DuplicateHeaders.xlsx"
            val blankColumnPath = basePath + "BlankColumn.xlsx"
            val blankRowPath = basePath + "BlankRow.xlsx"
            val blankCellPath = basePath + "BlankCells.xlsx"
            val invalidPath = "INVALID_FILE_PATH.xlsx"
            val dateFormat = SimpleDateFormat ("MM/dd/yyyy")

            data class TestDataClass(
                val string1: String,
                val string2: String,
                val int1: Int,
                val date1: Date
            )

            fun indexedProducer(rowMap: Map<Int, Cell>): TestDataClass {
                return TestDataClass(
                        string1 = getFromCellOrThrow(rowMap[0]),
                        string2 = getFromCellOrThrow(rowMap[1]),
                        int1 = getFromCellOrThrow<Double>(rowMap[2]).roundToInt(),
                        date1 = getFromCellOrThrow(rowMap[3])
                )
            }

            fun blankColumnIndexedProducer(rowMap: Map<Int, Cell>): TestDataClass {
                return TestDataClass(
                        string1 = getFromCellOrThrow(rowMap[0]),
                        string2 = getFromCellOrThrow(rowMap[1]),
                        int1 = getFromCellOrThrow<Double>(rowMap[3]).roundToInt(),
                        date1 = getFromCellOrThrow(rowMap[4])
                )
            }

            fun namedProducer(rowMap: Map<String, Cell>): TestDataClass {
                return TestDataClass(
                        string1 = getFromCellOrThrow(rowMap["StringCol1"]),
                        string2 = getFromCellOrThrow(rowMap["StringCol2"]),
                        int1 = getFromCellOrThrow<Double>(rowMap["IntCol"]).roundToInt(),
                        date1 = getFromCellOrThrow(rowMap["DateCol"])
                )
            }

            fun defaultIndexedProducer(rowMap: Map<Int, Cell>): TestDataClass {
                return TestDataClass(
                        string1 = getFromCellOrThrow(rowMap[0]),
                        string2 = getFromCellOrDefault(rowMap[1], "Row1Col2"),
                        int1 = getFromCellOrDefault<Double>(rowMap[2], 2.0).roundToInt(),
                        date1 = getFromCellOrThrow(rowMap[3])
                )
            }

            val expectedResults = listOf(
                    TestDataClass("Row1Col1", "Row1Col2", 1, dateFormat.parse("1/2/1996")),
                    TestDataClass("Row2Col1", "Row2Col2", 2, dateFormat.parse("3/4/1997")),
                    TestDataClass("Row3Col1", "Row3Col2", 3, dateFormat.parse("5/6/1998"))
            )

            "read a valid file without exception" {
                readWorkbook(happyPathPath)
            }

            "throw exception on invalid file" {
                shouldThrow<IOException> {
                    readWorkbook(invalidPath)
                }
            }

            "successfully parse a valid file with an indexed producer" {
                val parseResults = sheetToDataClasses(
                    sheet = readWorkbook(happyPathPath).first(),
                    dataProducer = ::indexedProducer,
                    excludeHeader = true
                )
                expectedResults shouldBe parseResults.toList()
            }

            "successfully parse a valid file with a named producer" {
                val parseResults = sheetToDataClasses(
                        sheet = readWorkbook(happyPathPath).first(),
                        dataProducer = ::namedProducer
                )
                expectedResults shouldBe parseResults.toList()
            }

            "correctly ignore duplicate headers when specified" {
                val parseResults = sheetToDataClasses(
                        sheet = readWorkbook(duplicateHeadersPath).first(),
                        dataProducer = ::namedProducer,
                        ignoreDuplicateHeaders = true
                )
                expectedResults shouldBe parseResults.toList()
            }

            "throw exception on duplicate headers when not specified to ignore" {
                shouldThrow<IllegalArgumentException> {
                    sheetToDataClasses(
                            sheet = readWorkbook(duplicateHeadersPath).first(),
                            dataProducer = ::namedProducer
                    ).first()
                }
            }

            "successfully parse a file with a blank column using an indexed producer" {
                val parseResults = sheetToDataClasses(
                        sheet = readWorkbook(blankColumnPath).first(),
                        dataProducer = ::blankColumnIndexedProducer,
                        excludeHeader = true
                )
                expectedResults shouldBe parseResults.toList()
            }

            "successfully parse a file with a blank column using an named producer" {
                val parseResults = sheetToDataClasses(
                        sheet = readWorkbook(blankColumnPath).first(),
                        dataProducer = ::namedProducer
                )
                expectedResults shouldBe parseResults.toList()
            }

            "successfully parse a file with a blank rows" {
                val parseResults = sheetToDataClasses(
                        sheet = readWorkbook(blankRowPath).first(),
                        dataProducer = ::indexedProducer,
                        excludeHeader = true
                )

                expectedResults shouldBe parseResults.toList()
            }

            "successfully use default values for blank cells" {
                val parseResults = sheetToDataClasses(
                        sheet = readWorkbook(blankCellPath).first(),
                        dataProducer = ::defaultIndexedProducer,
                        excludeHeader = true
                )

                expectedResults shouldBe parseResults.toList()
            }
        }
    }
}
