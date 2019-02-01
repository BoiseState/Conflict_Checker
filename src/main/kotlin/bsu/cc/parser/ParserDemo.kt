package bsu.cc.parser

import bsu.cc.data_classes.DemoDataClass
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.lang.reflect.Type
import kotlin.reflect.KClass

class ParserDemo {
    companion object {
        fun demoParse(fileName: String) {
            val file = FileInputStream(File(fileName))
            val sheet = XSSFWorkbook(file).getSheetAt(0)
            val rows = sheet.iterator()

            val row = rows.asSequence().toList()[1]
            val dClass: DemoDataClass = rowToDataClass(row, ::demoDataClassProducer)
            println(dClass.field1)
            println(dClass.field2)
            println(dClass.field3)
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

        private fun rowToCellMap(row: Row): Map<Int, Cell> {
            val output = HashMap<Int, Cell>()
            row.iterator().withIndex().forEachRemaining { indexedVal ->
                output[indexedVal.index] = indexedVal.value
            }
            return output
        }

        fun <T> rowToDataClass(
                row: Row,
                dataProducer: (rowMap: Map<Int, Cell>) -> T
        ): T {
            return dataProducer(rowToCellMap(row))
        }

        fun demoDataClassProducer(rowMap: Map<Int, Cell>): DemoDataClass {
            return DemoDataClass(
                field1 = getFromCellOrThrow(rowMap[0]),
                field2 = getFromCellOrThrow(rowMap[1]),
                field3 = getFromCellOrThrow(rowMap[2])
            )
        }

        private inline fun <reified T : Any> getFromCellOrThrow(cell: Cell?): T {
            if(cell == null) {
                throw IllegalArgumentException("Cell is null")
            }

            val expectedCellType = typeToCellType(T::class.java)
            if(expectedCellType != cell.cellType) {
                throw IllegalArgumentException("Attempting to get $expectedCellType from cell of type ${cell.cellType}")
            }

            return when(cell.cellType) {
                CellType.STRING -> cell.stringCellValue as T
                CellType.NUMERIC -> cell.numericCellValue as T
                else -> throw IllegalArgumentException("Attempting to get cell value for not string/numeric")
            }
        }

        private fun typeToCellType(kotlinType: Type): CellType {
            return when(kotlinType) {
                Int::class.java -> CellType.NUMERIC
                Double::class.java -> CellType.NUMERIC
                Number::class.java -> CellType.NUMERIC
                String::class.java -> CellType.STRING
                else -> CellType._NONE
            }
        }

    }
}