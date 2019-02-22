package bsu.cc.constraints

import bsu.cc.schedule.ClassSchedule

/**
 * list equality depends on position, so sort sub-lists first
 * map returns list, so convert back to set
 * sets equality does not depend on position
 */
fun checkOverlapsAreEqual(actual: Set<List<ClassSchedule>>, expected: Set<List<ClassSchedule>>): Boolean
 = actual.map { it.sorted() }.toSet() == expected.map { it.sorted() }.toSet()
