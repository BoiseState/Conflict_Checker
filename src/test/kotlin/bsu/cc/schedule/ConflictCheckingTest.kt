package bsu.cc.schedule
import bsu.cc.constraints.checkOverlapsAreEqual
import bsu.cc.schedule.*
import io.kotlintest.fail
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
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
    }
}

fun createDummyClass(index: Int, hour1: Int, min1: Int, hour2: Int, min2: Int): ClassSchedule
        = ClassSchedule(
        startTime = LocalTime.of(hour1, min1),
        endTime = LocalTime.of(hour2, min2),
        meetingDays = setOf(DayOfWeek.MONDAY),
        meetingDates = DateInterval(
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2)),
        subject = "ECE$index",
        catalogNumber = "330",
        section = "1",
        room = "123",
        instructors = setOf(Instructor("Test", "Joe")),
        description = "dummy"
)
