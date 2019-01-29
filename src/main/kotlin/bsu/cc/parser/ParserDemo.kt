package bsu.cc.parser

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

class ParserDemo {
    companion object {
        fun demoParse(fileName: String) {
            val file = FileInputStream(File(fileName))
            val sheet = XSSFWorkbook(file).getSheetAt(0)
            val rows = sheet.iterator()

            rows.forEachRemaining { row ->
                printRowCells(row)
                println()
            }
        }

        private fun printRowCells(row: Row) {
            row.iterator().forEachRemaining { cell ->
                val printVal = when(cell.cellType) {
                    CellType.STRING -> cell.stringCellValue
                    CellType.NUMERIC -> cell.numericCellValue.toString()
                    else -> "UNPARSABLE CELL"
                }
                print("$printVal, ")
            }
        }
    }
}