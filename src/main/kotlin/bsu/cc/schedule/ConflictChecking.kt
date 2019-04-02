package bsu.cc.schedule

import bsu.cc.constraints.ClassConstraint
import com.brein.time.timeintervals.collections.ListIntervalCollection
import com.brein.time.timeintervals.indexes.IntervalTree
import com.brein.time.timeintervals.indexes.IntervalTreeBuilder
import com.brein.time.timeintervals.indexes.IntervalValueComparator
import java.time.DayOfWeek
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
        = (classes.map { tree.overlap(it) }.filter { it.size > 1}.toSet() as Set<List<ClassSchedule>>)
        .flatMap(::findDateConflicts).toSet()

fun checkConstraints(classes: Collection<ClassSchedule>,
                     constraints: Collection<ClassConstraint>): Map<ClassConstraint, Set<List<ClassSchedule>>> {
    val conflicts = HashMap<ClassConstraint, List<ClassSchedule>>()

    constraints.forEach { constraint ->
        conflicts[constraint] = classes.filter { constraint.classes.contains(it.classString) }.toList()
    }

    return conflicts.mapValues { considerDaysOfWeek(it.value) }
}

fun checkRooms(classes: Collection<ClassSchedule>): Map<String, Set<List<ClassSchedule>>>
   = classes.groupBy { it.room }
        .filterKeys { it.trim() != "" }
        .mapValues { considerDaysOfWeek(it.value) }
        .filterValues { !it.isEmpty() }

fun checkInstructors(classes: Collection<ClassSchedule>): Map<Instructor, Set<List<ClassSchedule>>> {
    val instructors = HashMap<Instructor, MutableList<ClassSchedule>>()
    classes.forEach { c ->
        c.instructors.forEach { instructor ->
            val taughtClasses = instructors.getOrElse(instructor, ::ArrayList)
            taughtClasses.add(c)
            instructors[instructor] = taughtClasses
        }
    }

    val generalInstructor = Instructor("STAFF", "STAFF")
    return instructors.filterKeys { it != generalInstructor }
            .mapValues { considerDaysOfWeek(it.value) }
            .filterValues { !it.isEmpty() }
}

internal fun considerDaysOfWeek(classes: List<ClassSchedule>): Set<List<ClassSchedule>> {
    val dayMap = HashMap<DayOfWeek, MutableList<ClassSchedule>>()

    classes.forEach { c ->
        c.meetingDays.forEach { day ->
            val classesOnDay = dayMap.getOrElse(day, ::ArrayList)
            classesOnDay.add(c)
            dayMap[day] = classesOnDay
        }
    }

    val conflicts = HashSet<List<ClassSchedule>>()

    dayMap.values.forEach { classesOnDay ->
        val tree = buildScheduleIntervalTree()
        tree.addAll(classesOnDay)
        conflicts.addAll(checkForOverlaps(classesOnDay, tree))
    }

    return conflicts
}

/**
 * Brute force method for determining if the classes that conflict in time also conflict in date.
 * Ideally, the size of a conflict should be very small, making the brute force nature a non issue.
 */
internal fun findDateConflicts(classes: Collection<ClassSchedule>): Set<List<ClassSchedule>> {
    val conflicts = HashSet<ArrayList<ClassSchedule>>()

    classes.forEach { curr ->
        val conflict = ArrayList<ClassSchedule>()

        classes.forEach { c ->

            if (curr.meetingDates.overlaps(c.meetingDates)) {
                conflict.add(c)
            }
        }

        if (conflict.size > 1) {
            conflicts.add(conflict)
        }
    }

    return conflicts
}


