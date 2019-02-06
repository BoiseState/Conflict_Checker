package bsu.cc.example

import com.brein.time.timeintervals.collections.ListIntervalCollection
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder.*
import com.brein.time.timeintervals.intervals.IntegerInterval
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class IntervalTreeTest : WordSpec() {
    init {

        "An interval tree" should {
            "work?" {
                val tree = IntervalTreeBuilder.newBuilder()
                        .usePredefinedType(IntervalType.INTEGER)
                        .collectIntervals { ListIntervalCollection() }
                        .build()


                val data = listOf(
                        IntegerInterval(1, 3),
                        IntegerInterval(4, 7),
                        IntegerInterval(6, 8)
                )
                data.forEach { tree.add(it) }

                tree.overlap(data[0]).size.shouldBe(1) //returns [data[0]]
                tree.overlap(data[1]).size.shouldBe(2) //returns [data[1], data[2]]
                tree.overlap(data[2]).size.shouldBe(2) //returns [data[1], data[2]]
                tree.find(data[2]).size.shouldBe(1) //returns [data[2]]
            }
        }
    }
}
