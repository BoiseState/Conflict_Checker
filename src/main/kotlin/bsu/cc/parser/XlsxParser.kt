package bsu.cc.parser

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.reflect.Type


fun readWorkbook(fileName: String): XSSFWorkbook {
    val file = FileInputStream(File(fileName))
    return XSSFWorkbook(file)
}

fun <T> sheetToDataClasses(
        sheet: Sheet,
        dataProducer: (namedRowMap: Map<String, Cell>) -> T,
        ignoreDuplicateHeaders: Boolean = false
): Sequence<T> {
    val headerMap = sheetHeaderMap(sheet)
    fun translatedProducer(indexedRowMap: Map<Int, Cell>): T {
        return dataProducer(indexedToNamedRowMap(indexedRowMap, headerMap, ignoreDuplicateHeaders))
    }
    return sheetToDataClasses(sheet, ::translatedProducer, true)
}

fun <T> sheetToDataClasses(
        sheet: Sheet,
        dataProducer: (indexedRowMap: Map<Int, Cell>) -> T,
        excludeHeader: Boolean? = null
): Sequence<T> {
    return sheet.iterator().asSequence().withIndex().filter { (index, _) ->
        excludeHeader == null || !(excludeHeader && index == 0)
    }.map { (_, row) ->
        rowToDataClass(row, dataProducer)
    }
}

private fun indexedToNamedRowMap(
        indexedRowMap: Map<Int, Cell>,
        colNameMap: Map<Int, String>,
        ignoreDuplicateColumns: Boolean = false
): Map<String, Cell> {
    val output = HashMap<String, Cell>()
    indexedRowMap.keys.forEach { index ->
        val colName = colNameMap[index]
        val rowCell = indexedRowMap[index]
        if(colName == null) {
            throw IllegalArgumentException("Accessing column index out of bounds of headers")
        }
        if(rowCell == null) {
            throw IllegalStateException("Key not in map") //This should never happen, but needed for smartcast
        }
        if(!output.containsKey(colName) || !ignoreDuplicateColumns) {
            output[colName] = rowCell
        }
    }
    return output
}


fun sheetHeaderMap(sheet: Sheet): Map<Int, String> {
    val output = HashMap<Int, String>()
    sheet.first().cellIterator().withIndex().forEachRemaining { (index, cell) ->
        if(cell.cellType != CellType.STRING) {
            throw IllegalArgumentException("Sheet header has non-string in header")
        }
        output[index] = cell.stringCellValue
    }
    return output
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

inline fun <reified T : Any> getFromCellOrDefault(cell: Cell?, default: T): T {
    if(cell == null) {
        return default
    }

    val expectedCellType = typeToCellType(T::class.java)
    if(expectedCellType != cell.cellType) {
        return default
    }

    return when(cell.cellType) {
        CellType.STRING -> cell.stringCellValue as T
        CellType.NUMERIC -> cell.numericCellValue as T
        else -> default
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

private fun rowToCellMap(row: Row): Map<Int, Cell> {
    val output = HashMap<Int, Cell>()
    row.iterator().withIndex().forEachRemaining { indexedVal ->
        output[indexedVal.index] = indexedVal.value
    }
    return output
}
