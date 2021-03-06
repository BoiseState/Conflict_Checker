package bsu.cc.parser

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.reflect.Type
import java.util.*
import org.apache.poi.ss.usermodel.*



fun readWorkbook(fileName: String): XSSFWorkbook {
    return XSSFWorkbook(FileInputStream(File(fileName)))
}

fun <T> sheetToDataClasses(
        sheet: Sheet,
        dataProducer: (namedRowMap: Map<String, Cell>) -> T,
        ignoreDuplicateHeaders: Boolean = false,
        rowFilter: (row: Row) -> Boolean = { _ -> true }
): Sequence<T> {
    val headerMap = sheetHeaderMap(sheet)
    fun translatedProducer(indexedRowMap: Map<Int, Cell>): T {
        return dataProducer(indexedToNamedRowMap(indexedRowMap, headerMap, ignoreDuplicateHeaders))
    }
    return sheetToDataClasses(sheet, ::translatedProducer, true, rowFilter)
}

fun <T> sheetToDataClasses(
        sheet: Sheet,
        dataProducer: (indexedRowMap: Map<Int, Cell>) -> T,
        excludeHeader: Boolean? = null,
        rowFilter: (row: Row) -> Boolean = { _ -> true }
): Sequence<T> {
    return sheet.iterator().asSequence().withIndex().filter { (_, row) ->
        rowFilter(row)
    }.filter { (_, row) ->
        row.cellIterator().asSequence().filter{cell -> cell.cellType != CellType.BLANK}.toList().isNotEmpty() //Ignore blank rows
    }.filter { (index, _) ->
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
        if(!output.containsKey(colName)) {
            output[colName] = rowCell
        } else if(!ignoreDuplicateColumns) {
            throw IllegalArgumentException("Column map contains duplicate column names")
        }
    }
    return output
}

fun sheetHeaderMap(sheet: Sheet): Map<Int, String> {
    val output = HashMap<Int, String>()
    val headerRow = sheet.first()
    //Can't use foreach iterator because it skips empty columns, which causes column indices to be wrong
    (0 until headerRow.lastCellNum).forEach{index ->
        if(headerRow.getCell(index) != null) {
            val cell = headerRow.getCell(index)
            if(cell != null) {
                if(cell.cellType != CellType.STRING) {
                    throw IllegalArgumentException("Sheet has non-string in header")
                }
                output[index] = cell.stringCellValue
            }
        }
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
        CellType.NUMERIC -> if(T::class.java == Date::class.java) cell.dateCellValue as T else cell.numericCellValue as T
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
        CellType.NUMERIC -> if(T::class.java == Date::class.java) cell.dateCellValue as T else cell.numericCellValue as T
        else -> default
    }
}

fun typeToCellType(javaType: Type): CellType {
    return when(javaType) {
        Int::class.java, Integer::class.java  -> throw IllegalArgumentException("Numeric values in Apache POI are treated as doubles, Ints are not supported")
        java.lang.Double::class.java -> CellType.NUMERIC
        Number::class.java -> CellType.NUMERIC
        Date::class.java -> CellType.NUMERIC //Dates are stored as numbers
        String::class.java -> CellType.STRING
        else -> CellType._NONE
    }
}

private fun rowToCellMap(row: Row): Map<Int, Cell> {
    val output = HashMap<Int, Cell>()
    //Can't use foreach iterator because it skips empty columns, which causes column indices to be wrong
    (0 until row.lastCellNum).forEach{index ->
        if(row.getCell(index) != null) {
            output[index] = row.getCell(index)
        }
    }

    return output
}

fun highlightRow(
        sheet: Sheet,
        rowIndex: Int,
        color: IndexedColors,
        extendToMatchHeader: Boolean = false,
        colIndex: Short? = null
) {
    val workbook = sheet.workbook
    val highlightStyle = workbook.createCellStyle()
    highlightStyle.fillForegroundColor = color.index
    highlightStyle.fillPattern = FillPatternType.SOLID_FOREGROUND

    if(colIndex != null) {
        applyStyleToRow(sheet, rowIndex, highlightStyle, colIndex, (colIndex + 1).toShort())
    } else {
        val highlightToCol = if (extendToMatchHeader) sheet.getRow(0).lastCellNum else sheet.getRow(rowIndex).lastCellNum
        applyStyleToRow(sheet, rowIndex, highlightStyle, highlightToCol = highlightToCol)
    }
}

fun applyStyleToRow(sheet: Sheet, rowIndex: Int, cellStyle: CellStyle, startAtCol: Short? = null, highlightToCol: Short? = null) {
    val row = sheet.getRow(rowIndex)?: throw IllegalArgumentException("Sheet has no row at given index")
    val boundIndex = highlightToCol ?: row.lastCellNum
    val startIndex = startAtCol ?: 0

    //Can't use iterator because it skips empty cells
    (startIndex until boundIndex).forEach{ index ->
        val cell = row.getCell(index)?: row.createCell(index)
        cell.cellStyle = cellStyle
    }
}


