package bsu.cc.parser

import bsu.cc.ConfigurationKeys
import bsu.cc.constraints.ClassConstraint
import bsu.cc.constraints.readConstraintFile
import bsu.cc.schedule.*
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import tornadofx.ConfigProperties
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

const val MEETING_DATES_CELL_INDEX = 16
val colorSet = setOf(
        IndexedColors.RED,
        IndexedColors.LIGHT_BLUE,
        IndexedColors.LIGHT_YELLOW,
        IndexedColors.LIGHT_TURQUOISE
)

fun displayConflictsOnNewSheet(workbook: XSSFWorkbook, classSchedules: List<ClassSchedule>, constraints: List<ClassConstraint>): XSSFWorkbook {
    val instructorConflicts = checkInstructors(classSchedules)
    val roomConflicts = checkRooms(classSchedules)
    val constraintConflicts = checkConstraints(classSchedules, constraints)

    val conflictsSheet = workbook.createSheet("Conflicts")

    val headerStyle = workbook.createCellStyle()
    val headerFont = workbook.createFont()
    headerFont.fontName = "Arial"
    headerFont.fontHeightInPoints = 24
    headerStyle.setFont(headerFont)

    var rowIndex = 0
    rowIndex = addConflicts(conflictsSheet, rowIndex, "Instructor Conflicts", headerStyle, instructorConflicts.mapKeys { "${it.key.lastName}, ${it.key.firstName}" })
    rowIndex = addConflicts(conflictsSheet, rowIndex, "Room Conflicts", headerStyle, roomConflicts)
    addConflicts(conflictsSheet, rowIndex, "Constraint Conflicts", headerStyle, constraintConflicts.mapKeys { it.key.classes.joinToString() })

    0.rangeTo(ClassSchedule.xlsxHeaders.size + 3).forEach { colIndex ->
        conflictsSheet.autoSizeColumn(colIndex)
    }

    return workbook
}

fun addConflicts(sheet: XSSFSheet, startIndex: Int, headerName: String, headerStyle: XSSFCellStyle, conflicts: Map<String, Set<List<ClassSchedule>>>): Int {
    var index = startIndex + 1 //One row of padding
    val header = sheet.createRow(index++)
    val headerCell = header.createCell(0)
    headerCell.setCellValue(headerName)
    headerCell.cellStyle = headerStyle
    val colNames = sheet.createRow(index++)
    ClassSchedule.xlsxHeaders.withIndex().forEach{ (index, header) ->
        colNames.createCell(index + 2).setCellValue(header)
    }

    conflicts.filterValues { it.isNotEmpty() }.keys.forEach{ key ->
        val constraintRow = sheet.createRow(index++)
        constraintRow.createCell(0).setCellValue(key)
        var conflictIndex = 1
        (conflicts[key]?: throw IllegalStateException("Key does not have value")).forEach { classSchedules ->
            val conflictRow = sheet.createRow(index++)
            conflictRow.createCell(1).setCellValue("Conflict ${conflictIndex++}")
            classSchedules.forEach { classSchedule ->
                classScheduleToRow(classSchedule, sheet, index++, 2)
            }
        }
    }
    return index
}


fun highlightConflictsOnNewSheet(workbook: XSSFWorkbook, classSchedules: List<ClassSchedule>, constraints: List<ClassConstraint>): XSSFWorkbook {
    val conflicts = checkConstraints(classSchedules, constraints)

    val constraintColorMap = constraints.mapIndexed { index, classConstraint ->
        Pair(classConstraint, colorSet.elementAt(index % colorSet.size))
    }.toMap()

    val highlightSheet = workbook.createSheet("Highlighted Schedule")
    val headerRow = highlightSheet.createRow(0)
    ClassSchedule.xlsxHeaders.withIndex().forEach{ (index, header) ->
        headerRow.createCell(index).setCellValue(header)
    }

    classSchedules.withIndex().forEach { (index, classSchedule) ->
        val rowIndex = index + 1
        classScheduleToRow(classSchedule, highlightSheet, rowIndex)
        val violatedConstraints = conflicts.filterKeys { cc -> conflicts[cc]?.flatten()?.contains(classSchedule)?: false }.keys
        if(violatedConstraints.isNotEmpty()) {
            highlightRow(highlightSheet, rowIndex, constraintColorMap[violatedConstraints.elementAt(0)]?:IndexedColors.RED)
        }
    }
    headerRow.firstCellNum.rangeTo(headerRow.lastCellNum).forEach { colIndex ->
        highlightSheet.autoSizeColumn(colIndex)
    }

    return workbook
}

fun writeWorkbook(workbook: XSSFWorkbook, fileName: String) {
    FileOutputStream(fileName).use {
        workbook.write(it)
    }
}

fun identifyAndWriteConflicts(fileName: String, constraintsFileName: String, sheetIndex: Int = 0) : String {
    val workbook = readWorkbook(fileName)
    val scheduleSheet = workbook.getSheetAt(sheetIndex) ?: throw IllegalArgumentException("No sheet present at given index")
    val constraints = readConstraintFile(File(constraintsFileName))

    val classSchedules = sheetToDataClasses(
            sheet = scheduleSheet,
            dataProducer = ::classScheduleProducer,
            rowFilter = ::incompleteRowFilter,
            ignoreDuplicateHeaders = true
    ).toList()

    val highlightedWB = highlightConflictsOnNewSheet(workbook, classSchedules, constraints)
    val finalWB = displayConflictsOnNewSheet(highlightedWB, classSchedules, constraints)
    val newFileName = fileName.removeRange((fileName.length - 5) until (fileName.length)) + "Higlighted.xlsx"
    writeWorkbook(finalWB,  newFileName)

    return newFileName
}

private fun incompleteRowFilter(row: Row): Boolean {
    return row.getCell(MEETING_DATES_CELL_INDEX)?.cellType?: CellType.BLANK != CellType.BLANK
}



