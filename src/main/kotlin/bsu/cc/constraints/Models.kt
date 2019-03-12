package bsu.cc.constraints

enum class ConstraintPriority(val prettyString: String) {
    PRIORITY("priority"),
    NON_PRIORITY("non-priority")
}

fun createPriorityFrom(str: String): ConstraintPriority {
    return when (str) {
        ConstraintPriority.PRIORITY.prettyString ->
            ConstraintPriority.PRIORITY
        ConstraintPriority.NON_PRIORITY.prettyString ->
            ConstraintPriority.NON_PRIORITY
        else -> throw IllegalArgumentException("Unknown priority: ${str}")
    }
}


data class ClassConstraint(
        val id: Int,
        val priority: ConstraintPriority,
        val classes: Set<String>
)