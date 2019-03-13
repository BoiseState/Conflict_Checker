package bsu.cc.parser

import bsu.cc.Configuration
import bsu.cc.constraints.ClassConstraint
import bsu.cc.constraints.readConstraintFile
import bsu.cc.schedule.*
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
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

fun displayConflictsOnNewSheet(workbook: XSSFWorkbook, scheduleSheet: XSSFSheet, constraints: List<ClassConstraint>): XSSFWorkbook {
    val classSchedules = sheetToDataClasses(
            sheet = scheduleSheet,
            dataProducer = ::classScheduleProducer,
            rowFilter = ::incompleteRowFilter,
            ignoreDuplicateHeaders = true
    ).toList()
    val conflicts = checkConstraints(classSchedules, constraints)

    val conflictsSheet = workbook.createSheet("Conflicts")
    val headerRow = conflictsSheet.createRow(0)
    headerRow.createCell(0).setCellValue("Constraint")
    ClassSchedule.xlsxHeaders.withIndex().forEach{ (index, header) ->
        headerRow.createCell(index + 2).setCellValue(header)
    }

    var rowIndex = 1
    conflicts.keys.forEach { constraint ->
        val constraintRow = conflictsSheet.createRow(rowIndex++)
        constraintRow.createCell(0).setCellValue(constraint.classes.joinToString())
        var conflictIndex = 1
        (conflicts[constraint]?: throw IllegalStateException("Key does not have value")).forEach { classSchedules ->
            val conflictRow = conflictsSheet.createRow(rowIndex++)
            conflictRow.createCell(1).setCellValue("Conflict ${conflictIndex++}")
            classSchedules.forEach { classSchedule ->
                classScheduleToRow(classSchedule, conflictsSheet, rowIndex++, 2)
            }
        }
    }

    0.rangeTo(headerRow.lastCellNum).forEach { colIndex ->
        conflictsSheet.autoSizeColumn(colIndex)
    }

    return workbook
}

fun highlightConflictsOnNewSheet(workbook: XSSFWorkbook, scheduleSheet: XSSFSheet, constraints: List<ClassConstraint>): XSSFWorkbook {
    val classSchedules = sheetToDataClasses(
            sheet = scheduleSheet,
            dataProducer = ::classScheduleProducer,
            rowFilter = ::incompleteRowFilter,
            ignoreDuplicateHeaders = true
    ).toList()
    val conflicts = checkConstraints(classSchedules, constraints)
    conflicts.keys.forEach{ key -> println("$key : ${conflicts[key]}")}

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

fun identifyAndWriteConflicts(fileName: String, sheetIndex: Int = 0) : ArrayList<ArrayList<ClassSchedule>> {
    val workbook = readWorkbook(fileName)
    val scheduleSheet = workbook.getSheetAt(sheetIndex) ?: throw IllegalArgumentException("No sheet present at given index")
    val constraints = readConstraintFile(File(Configuration.constraintsFilePath))
    val highlightedWB = highlightConflictsOnNewSheet(workbook, scheduleSheet, constraints)
    val finalWB = displayConflictsOnNewSheet(highlightedWB, scheduleSheet, constraints)
    writeWorkbook(finalWB, fileName.removeRange((fileName.length - 5) until (fileName.length)) + "Higlighted.xlsx" )

    val classSchedules = sheetToDataClasses(
            sheet = scheduleSheet,
            dataProducer = ::classScheduleProducer,
            rowFilter = ::incompleteRowFilter,
            ignoreDuplicateHeaders = true
    ).toList()

    return getStringConflicts(classSchedules, constraints)
}

private fun incompleteRowFilter(row: Row): Boolean {
    return row.getCell(MEETING_DATES_CELL_INDEX)?.cellType?: CellType.BLANK != CellType.BLANK
}



