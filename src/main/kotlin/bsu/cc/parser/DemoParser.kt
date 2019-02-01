package bsu.cc.parser

import bsu.cc.data_classes.DemoDataClass
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

class DemoParser {
    companion object {
        fun demoParse(fileName: String) {
            val dClasses = XlsxParser.fileToDataClasses(
                    fileName,
                    ::demoDataClassProducer,
                    setOf(0) //Ignores the header row 
            )
            dClasses.forEach { dClass ->
                println(dClass.field1)
                println(dClass.field2)
                println(dClass.field3)
            }
        }

        fun demoDataClassProducer(rowMap: Map<Int, Cell>): DemoDataClass {
            return DemoDataClass(
                    field1 = XlsxParser.getFromCellOrThrow(rowMap[0]),
                    field2 = XlsxParser.getFromCellOrThrow(rowMap[1]),
                    field3 = XlsxParser.getFromCellOrThrow(rowMap[2])
            )
        }
    }
}
