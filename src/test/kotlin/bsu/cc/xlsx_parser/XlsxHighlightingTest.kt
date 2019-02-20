package bsu.cc.xlsx_parser

import bsu.cc.parser.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import java.io.FileOutputStream
import java.lang.IllegalArgumentException

class XlsxHighlightingTest : WordSpec() {
    private val basePath = "src/test/resources/xlsx/"
    private val DEFAULT_SHEET_INDEX = 0

    init {
        "XLSX highlighting" should {

            val happyPathPath = basePath + "HappyPath.xlsx"
            val unevenRowLengthPath = basePath + "UnevenRowLengths.xlsx"
            val tempOutputPath = basePath + "HighlightingTemp.xlsx"

            fun verifyHighlighting(filePath: String, sheetIndex: Int, rowIndex: Int, cellIndices: List<Int>, color: IndexedColors) {
                val workbook = readWorkbook(filePath)
                val row = workbook.getSheetAt(sheetIndex).getRow(rowIndex);
                (0 until ((cellIndices.max() ?: throw IllegalArgumentException("Empty cell indices")) + 1)).filter { index -> cellIndices.contains(index) }.forEach { index ->
                    row.getCell(index).cellStyle.fillForegroundXSSFColor.index shouldBe color.index
                }
            }

            fun writeTempWorkbook(workbook: Workbook): String {
                FileOutputStream(tempOutputPath).use {
                    workbook.write(it)
                }
                return tempOutputPath
            }

            "correctly highlight a single row" {
                val rowIndex = 0
                val color = IndexedColors.GREEN
                val workbook = readWorkbook(happyPathPath)
                val highlightRange = (0 until workbook.getSheetAt(DEFAULT_SHEET_INDEX).getRow(rowIndex).lastCellNum).toList()

                highlightRow(workbook, DEFAULT_SHEET_INDEX, 0, color)
                val tempWbPath = writeTempWorkbook(workbook)
                verifyHighlighting(tempWbPath, DEFAULT_SHEET_INDEX, 0, highlightRange, color)
            }

            "correctly highlight multiple equal length rows" {
                val rowIndices = 1 until 4
                val color = IndexedColors.RED
                val workbook = readWorkbook(happyPathPath)
                val highlightRange = (0 until workbook.getSheetAt(DEFAULT_SHEET_INDEX).getRow(rowIndices.first).lastCellNum).toList()
                rowIndices.forEach {rowIndex ->
                    highlightRow(workbook, DEFAULT_SHEET_INDEX, rowIndex, color)
                }
                val tempWbPath = writeTempWorkbook(workbook)
                rowIndices.forEach { rowIndex ->
                    verifyHighlighting(tempWbPath, DEFAULT_SHEET_INDEX, rowIndex, highlightRange, color)
                }
            }

            "correctly highlight multiple rows with multiple colors" {
                val redIndex = 1
                val blueIndex = 2
                val workbook = readWorkbook(happyPathPath)
                val highlightRange = (0 until workbook.getSheetAt(DEFAULT_SHEET_INDEX).getRow(blueIndex).lastCellNum).toList()
                highlightRow(workbook, DEFAULT_SHEET_INDEX, redIndex, IndexedColors.RED)
                highlightRow(workbook, DEFAULT_SHEET_INDEX, blueIndex, IndexedColors.BLUE)
                val tempWbPath = writeTempWorkbook(workbook)
                verifyHighlighting(tempWbPath, DEFAULT_SHEET_INDEX, redIndex, highlightRange, IndexedColors.RED)
                verifyHighlighting(tempWbPath, DEFAULT_SHEET_INDEX, blueIndex, highlightRange, IndexedColors.BLUE)
            }

            "correctly highlight a row with empty cells" {
                val rowIndex = 1
                val color = IndexedColors.YELLOW
                val workbook = readWorkbook(happyPathPath)
                val highlightRange = (0 until workbook.getSheetAt(DEFAULT_SHEET_INDEX).getRow(0).lastCellNum).toList()

                highlightRow(workbook, DEFAULT_SHEET_INDEX, rowIndex, color)
                val tempWbPath = writeTempWorkbook(workbook)
                verifyHighlighting(tempWbPath, DEFAULT_SHEET_INDEX, rowIndex, highlightRange, color)
            }

            "correctly highlight rows with uneven lengths without extension" {
                val shortRowIndex = 2
                val longRowIndex = 4
                val color = IndexedColors.RED
                val workbook = readWorkbook(unevenRowLengthPath)
                highlightRow(workbook, DEFAULT_SHEET_INDEX, shortRowIndex, color)
                highlightRow(workbook, DEFAULT_SHEET_INDEX, longRowIndex, color)
                val tempWbPath = writeTempWorkbook(workbook)
                verifyHighlighting(tempWbPath, DEFAULT_SHEET_INDEX, shortRowIndex, (0 until 3).toList(), color)
                verifyHighlighting(tempWbPath, DEFAULT_SHEET_INDEX, longRowIndex, (0 until 4).toList(), color)
                //No auto extension, so this cell should be blank
                workbook.getSheetAt(DEFAULT_SHEET_INDEX).getRow(shortRowIndex).lastCellNum shouldBe 3.toShort()
            }

            "correctly highlight rows with uneven lengths with extension" {
                val shortRowIndex = 2
                val longRowIndex = 4
                val color = IndexedColors.YELLOW
                val workbook = readWorkbook(unevenRowLengthPath)
                highlightRow(workbook, DEFAULT_SHEET_INDEX, shortRowIndex, color, true)
                highlightRow(workbook, DEFAULT_SHEET_INDEX, longRowIndex, color, true)
                val tempWbPath = writeTempWorkbook(workbook)
                val highlightRange = (0 until 4).toList()
                verifyHighlighting(tempWbPath, DEFAULT_SHEET_INDEX, shortRowIndex, highlightRange, color)
                verifyHighlighting(tempWbPath, DEFAULT_SHEET_INDEX, longRowIndex, highlightRange, color)
            }
        }
    }
}
