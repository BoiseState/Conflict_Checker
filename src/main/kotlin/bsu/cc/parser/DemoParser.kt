package bsu.cc.parser

import bsu.cc.data_classes.DemoDataClass
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream

class DemoParser {
    companion object {
        fun demoParse(fileName: String) {
            val sheet = readWorkbook(fileName).first()
//            val dClasses = sheetToDataClasses(
//                    sheet = sheet,
//                    dataProducer = ::demoDataClassIndexedProducer,
//                    excludeHeader = true //Ignores the header row
//            )

            val dClasses = sheetToDataClasses(
                    sheet = sheet,
                    dataProducer = ::demoDataClassNamedProducer
            )
            dClasses.forEach { dClass ->
                println(dClass.field1)
                println(dClass.field2)
                println(dClass.field3)
            }
        }

        private fun demoDataClassIndexedProducer(rowMap: Map<Int, Cell>): DemoDataClass {
            return DemoDataClass(
                    field1 = getFromCellOrThrow(rowMap[0]),
                    field2 = getFromCellOrThrow(rowMap[1]),
                    field3 = getFromCellOrThrow(rowMap[2])
            )
        }

        private fun demoDataClassNamedProducer(rowMap: Map<String, Cell>): DemoDataClass {
            return DemoDataClass(
                    field1 = getFromCellOrThrow(rowMap["Subject"]),
                    field2 = getFromCellOrThrow(rowMap["Catalog Nbr"]),
                    field3 = getFromCellOrThrow(rowMap["Descr"])
            )
        }
    }
}
