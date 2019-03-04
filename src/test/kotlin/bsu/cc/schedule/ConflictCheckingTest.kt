package bsu.cc.schedule
import bsu.cc.constraints.ConstraintPriority
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class ConflictCheckingTest : WordSpec() {
    init {
        "Single entity checking" should {
            val noOverlapSchedule = setOf(
                    createDummyClass(1, 10, 0, 11, 15),
                    createDummyClass(2, 11, 30, 12, 45),
                    createDummyClass(3, 1, 0, 2, 15),
                    createDummyClass(4, 3, 0, 5, 15),
                    createDummyClass(5, 5, 30, 6, 59),
                    createDummyClass(6, 7, 0, 7, 30)
            )
            val overlapingSchedule = setOf(
                    createDummyClass(1, 10, 0, 11, 15),
                    createDummyClass(2, 11, 0, 12, 45),
                    createDummyClass(3, 1, 0, 2, 15),
                    createDummyClass(4, 6, 0, 6, 15),
                    createDummyClass(5, 5, 30, 7, 0),
                    createDummyClass(6, 7, 0, 7, 30)
            )

            "detect no conflicts in valid schedule" {
                val tree = buildScheduleIntervalTree()
                tree.addAll(noOverlapSchedule)

                checkForOverlaps(noOverlapSchedule, tree).size.shouldBe(0)
            }

            "detect conflicts in invalid tree" {
                val overlap1 = listOf(
                        overlapingSchedule.elementAt(0),
                        overlapingSchedule.elementAt(1)
                )

                val overlap2 = listOf(
                        overlapingSchedule.elementAt(3),
                        overlapingSchedule.elementAt(4),
                        overlapingSchedule.elementAt(5)
                )

                val overlap3 = listOf(
                        overlapingSchedule.elementAt(3),
                        overlapingSchedule.elementAt(4)
                )

                val overlap4 = listOf(
                        overlapingSchedule.elementAt(5),
                        overlapingSchedule.elementAt(4)
                )

                val overlaps = setOf(
                        overlap1,
                        overlap2,
                        overlap3,
                        overlap4
                )

                val tree = buildScheduleIntervalTree()
                tree.addAll(overlapingSchedule)

                val collisions = checkForOverlaps(overlapingSchedule, tree)
                collisions.size.shouldBe(4)

                checkOverlapsAreEqual(collisions, overlaps).shouldBeTrue()
            }
        }

        "Class Constraint checking" should {
            val constraints = listOf(
                    createContraint(1, ConstraintPriority.PRIORITY, "cs121", "cs221", "cs321"),
                    createContraint(1, ConstraintPriority.PRIORITY, "ece230", "cs253"),
                    createContraint(1, ConstraintPriority.NON_PRIORITY, "math189", "cs221")
            )

            val classes = setOf(
                    createDummyClass("cs", "121", "1", "1:00", "1:45"),
                    createDummyClass("cs", "121", "2", "2:00", "2:45"),
                    createDummyClass("cs", "121", "3", "3:00", "3:45"),

                    createDummyClass("cs", "221", "1", "3:00", "3:45"),
                    createDummyClass("cs", "221", "2", "4:00", "4:45"),

                    createDummyClass("cs", "321", "3", "4:00", "4:45"),

                    createDummyClass("ece", "230", "1", "4:00", "4:45"),
                    createDummyClass("cs", "253", "1", "2:00", "2:45"),

                    createDummyClass("math", "189", "1", "4:00", "4:45")
            )

            val conflicts = checkConstraints(classes, constraints)

            "no conflicts should be empty" {
                conflicts.getValue(constraints[1]).isEmpty().shouldBeTrue()
            }

            "check single conflict" {
                val expected = setOf(
                        listOf(classes.elementAt(4), classes.elementAt(8))
                )

                val conflict = conflicts.getValue(constraints[2])

                checkOverlapsAreEqual(conflict, expected).shouldBeTrue()
            }

            "check several conflicts" {
                val expected = setOf(
                        listOf(classes.elementAt(2), classes.elementAt(3)),
                        listOf(classes.elementAt(4), classes.elementAt(5))
                )

                val conflict = conflicts.getValue(constraints[0])

                checkOverlapsAreEqual(conflict, expected).shouldBeTrue()
            }
        }

        "class date ranges" should {
            "not cause conflicts if no overlap" {
                val classes = listOf(
                        createDummyClass(1, 2),
                        createDummyClass(3, 4),
                        createDummyClass(5, 6)
                )

                findDateConflicts(classes).isEmpty().shouldBeTrue()
            }

            "cause conflicts if there is overlap" {
                val classes = listOf(
                        createDummyClass(1, 2),
                        createDummyClass(2, 3)
                )

                checkOverlapsAreEqual(findDateConflicts(classes), setOf(classes)).shouldBeTrue()
            }

            "find multiple conflicts with multiple date ranges: {" {
                val classes = listOf(
                        createDummyClass(1, 2, "1"),
                        createDummyClass(1, 2, "2"),
                        createDummyClass(3, 4, "1"),
                        createDummyClass(3, 4, "2")
                )

                val expected = setOf(
                        listOf(
                                classes[0],
                                classes[1]),
                        listOf(
                                classes[2],
                                classes[3])
                )

                checkOverlapsAreEqual(findDateConflicts(classes), expected).shouldBeTrue()

            }
        }
    }
}

