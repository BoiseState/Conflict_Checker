package bsu.cc.parser

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.lang.reflect.Type


fun <T> fileToDataClasses(
        fileName: String,
        dataProducer: (rowMap: Map<Int, Cell>) -> T,
        excludedRowIndices: Set<Int>? = null
): Sequence<T> {
    return fileToRows(fileName).asSequence().withIndex().filter { (index, _) ->
        excludedRowIndices == null || !excludedRowIndices.contains(index)
    }.map { (_, row) ->
        rowToDataClass(row, dataProducer)
    }
}

fun fileToRows(fileName: String): MutableIterator<Row> {
    val file = FileInputStream(File(fileName))
    val sheet = XSSFWorkbook(file).getSheetAt(0)
    return sheet.iterator()
}

fun <T> rowToDataClass(
        row: Row,
        dataProducer: (rowMap: Map<Int, Cell>) -> T
): T {
    return dataProducer(rowToCellMap(row))
}

inline fun <reified T : Any> getFromCellOrThrow(cell: Cell?): T {
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

fun typeToCellType(kotlinType: Type): CellType {
    return when(kotlinType) {
        Int::class.java -> CellType.NUMERIC
        Double::class.java -> CellType.NUMERIC
        Number::class.java -> CellType.NUMERIC
        String::class.java -> CellType.STRING
        else -> CellType._NONE
    }
}

fun printRowCells(row: Row) {
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
