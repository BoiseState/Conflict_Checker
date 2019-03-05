package bsu.cc.constraints

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException

class ConstraintFileTest : WordSpec() {
    val basePath = "src/test/resources/csv/"
    init {
        "Reading a file" should {

            val invalidFile = File(basePath + "invalid.csv")
            val validFile = File(basePath + "valid.csv")
            val validContents = listOf(
                    ClassConstraint(1, ConstraintPriority.PRIORITY, setOf("cs121", "cs221")),
                    ClassConstraint(2, ConstraintPriority.NON_PRIORITY, setOf("cs321", "cs421", "cs471"))
            )

            "not throw error with valid file" {
                readConstraintFile(validFile)
            }

            "read correct contents" {
                val contents = readConstraintFile(validFile)
                contents.forEachIndexed { index, classConstraint ->
                    classConstraint.shouldBe(validContents[index])
                }
            }

            "fail with invalid priority" {
                shouldThrow<IllegalArgumentException> {
                    readConstraintFile(invalidFile)
                }
            }

            "fail with non-existant file" {
                shouldThrow<IOException> {
                    readConstraintFile(File("IMMMMMMM HOPEFULLY NOT AN ACTUALLY FILE XDXDXDXD"))
                }
            }
        }

        "Writing a file" should {

            val outFile = File(basePath + "out.csv")
            val outContents = listOf(
                    ClassConstraint(1, ConstraintPriority.PRIORITY, setOf("cs121", "cs221")),
                    ClassConstraint(2, ConstraintPriority.NON_PRIORITY, setOf("cs321", "cs421", "cs471"))
            )

            "not fail with valid file" {
                writeConstraintsFile(outFile, outContents)
                outFile.exists().shouldBe(true)

                if (outFile.exists()) {
                    outFile.delete()
                }
            }

            "produce correct output" {
                writeConstraintsFile(outFile, outContents)
                val data = readConstraintFile(outFile)

                data.forEachIndexed { index, contsraint ->
                    contsraint.shouldBe(outContents[index])
                }

                if (outFile.exists()) {
                    outFile.delete()
                }
            }
        }

        "Constraint file IO" should {
            "be consistent when writing and reading" {
                val outFile = File(basePath + "out.csv")
                val validFile = File(basePath + "valid.csv")
                val validContents = listOf(
                        ClassConstraint(1, ConstraintPriority.PRIORITY, setOf("cs121", "cs221")),
                        ClassConstraint(2, ConstraintPriority.NON_PRIORITY, setOf("cs321", "cs421", "cs471"))
                )

                var data = readConstraintFile(validFile)
                data.forEachIndexed { index, contsraint ->
                    contsraint.shouldBe(validContents[index])
                }

                writeConstraintsFile(outFile, data)
                data = readConstraintFile(outFile)
                data.forEachIndexed { index, contsraint ->
                    contsraint.shouldBe(validContents[index])
                }

                if (outFile.exists()) {
                    outFile.delete()
                }
            }
        }
    }
}

