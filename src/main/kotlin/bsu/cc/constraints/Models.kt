package bsu.cc.constraints

enum class ConstraintPriority(val prettyString: String) {
    PRIORITY("priority"),
    NON_PRIORITY("non-priority"),
    IGNORE("ignore")
}

fun createPriorityFrom(str: String): ConstraintPriority {
    return when (str) {
        ConstraintPriority.PRIORITY.prettyString ->
            ConstraintPriority.PRIORITY
        ConstraintPriority.NON_PRIORITY.prettyString ->
            ConstraintPriority.NON_PRIORITY
        ConstraintPriority.IGNORE.prettyString ->
            ConstraintPriority.IGNORE
        else -> throw IllegalArgumentException("Unknown priority: ${str}")
    }
}


data class ClassConstraint(
        val id: Int,
        val priority: ConstraintPriority,
        val classes: Set<String>
)