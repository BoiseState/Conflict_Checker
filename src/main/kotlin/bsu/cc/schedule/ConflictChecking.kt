package bsu.cc.schedule

import com.brein.time.timeintervals.collections.ListIntervalCollection
import com.brein.time.timeintervals.indexes.IntervalTree
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder
import com.brein.time.timeintervals.indexes.IntervalValueComparator
import java.util.stream.Collectors

fun buildScheduleIntervalTree(): IntervalTree {
    val tree = IntervalTreeBuilder.newBuilder()
            .collectIntervals { ListIntervalCollection() }
            .build()

    // required for overlap operations
    tree.configuration.setValueComparator(IntervalValueComparator::compareInts)

    // required for find operations
    tree.configuration.setIntervalFilter { comp, val1, val2 ->
        comp.compare(val1.getNormStart(), val2.getNormStart()) == 0 &&
                comp.compare(val1.getNormEnd(), val2.getNormEnd()) == 0
    }
    return tree
}

@Suppress("UNCHECKED_CAST")
fun checkForOverlaps(classes: Collection<ClassSchedule>, tree: IntervalTree): Set<List<ClassSchedule>>
    = classes.map { tree.overlap(it) }.filter { it.size > 1}.toSet() as Set<List<ClassSchedule>>

