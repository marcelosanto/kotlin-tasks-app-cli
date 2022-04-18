data class Task(val priority: String, val date: String, val tag: String, val time: String, val task: MutableList<String>) {
    override fun toString(): String {
        return "Task: p: $priority, date: $date, tag: $tag, time: $time, task: $task"
    }
}