package bsu.cc.constraints

import de.siegmar.fastcsv.reader.CsvReader
import de.siegmar.fastcsv.writer.CsvWriter
import java.io.File
import java.nio.charset.StandardCharsets

fun readConstraintFile(file: File): MutableList<ClassConstraint> {
    val reader = CsvReader()
    val contraints = ArrayList<ClassConstraint>()

    reader.read(file, StandardCharsets.UTF_8).rows.map { row ->
        val id = row.originalLineNumber.toInt()
        val priority = createPriorityFrom(row.getField(0).toLowerCase())
        val classes = ArrayList<String>()
        for (i in 1 until row.fieldCount) {
            classes += row.getField(i).toLowerCase()
        }

        contraints += ClassConstraint(
                id = id,
                priority = priority,
                classes = classes
        )
    }

    return contraints
}

fun writeConstraintsFile(file: File, constraints: List<ClassConstraint>) {
    val data = constraints.map { row ->
        Array(1 + row.classes.size) { i ->
            when (i) {
                0 -> row.priority.prettyString
                else -> row.classes[i - 1]
            }
        }
    }

    CsvWriter().write(file, StandardCharsets.UTF_8, data)
}

