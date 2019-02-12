package bsu.cc.xlsx_parser

import bsu.cc.constraints.readConstraintFile
import bsu.cc.data_classes.DemoDataClass
import bsu.cc.parser.DemoParser
import bsu.cc.parser.getFromCellOrThrow
import bsu.cc.parser.readWorkbook
import bsu.cc.parser.sheetToDataClasses
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.apache.poi.ss.usermodel.Cell
import java.io.File

class XlsxParserTest : WordSpec() {
    val basePath = "src/test/resources/xlsx/"

    init {
        "Parsing an XLSX" should {

            val happyPathPath = basePath + "HappyPath"
            val duplicateHeadersPath = basePath + "DuplicateHeaders"
            val blankColumnPath = basePath + "BlankColumn"
            val blankRowWb = basePath + "BlankRow"

            data class TestDataClass(
                val string1: String,
                val string2: String,
                val int1: Int,
                val date1: String
            )

            fun indexedProducer(rowMap: Map<Int, Cell>): TestDataClass {
                return TestDataClass(
                        string1 = getFromCellOrThrow(rowMap[0]),
                        string2 = getFromCellOrThrow(rowMap[1]),
                        int1 = getFromCellOrThrow(rowMap[2]),
                        date1 = getFromCellOrThrow(rowMap[3])
                )
            }

            fun namedProducer(rowMap: Map<String, Cell>): TestDataClass {
                return TestDataClass(
                        string1 = getFromCellOrThrow(rowMap["StringCol1"]),
                        string2 = getFromCellOrThrow(rowMap["StringCol2"]),
                        int1 = getFromCellOrThrow(rowMap["IntCol"]),
                        date1 = getFromCellOrThrow(rowMap["DateCol"])
                )
            }

            val expectedResults = listOf(
                    TestDataClass("Row1Col1", "Row1Col2", 1, "1/2/1996"),
                    TestDataClass("Row2Col1", "Row2Col2", 2, "3/4/1996"),
                    TestDataClass("Row3Col1", "Row3Col2", 3, "5/6/1996")
            )

            "read a valid file without exception" {
                readWorkbook(happyPathPath)
            }

            "correctly parse a valid file" {
                val parseResults = sheetToDataClasses(
                    sheet = readWorkbook(happyPathPath).first(),
                    dataProducer = ::indexedProducer
                )
                parseResults.shouldBe(expectedResults)
//                parseResults.forEachIndexed { index, rowDataClass ->
//                    rowDataClass.shouldBe(expectedResults[index])
//                }
            }
        }
    }
}
