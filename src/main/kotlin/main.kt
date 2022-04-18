import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.datetime.*
import java.io.File

fun main() {
    val tasksList = mutableListOf<Task>()
    val txt = "src/task.json"
    val taskJson = File(txt)

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    val type = Types.newParameterizedType(List::class.java, Task::class.java)
    val taskAdapter = moshi.adapter<List<Task>>(type)

    if (taskJson.exists()) {
        val tasks = taskJson.readText()
        val tskList = taskAdapter.fromJson(tasks)

        tskList?.let {
            for (i in it) {
                tasksList.add(i)
            }
        }
    }



    while (true) {
        println("Input an action (add, print, edit, delete, end): ")

        when (readln()) {
            "add" -> {
                val priority = addTaskPriority()
                val date = addTaskDate()
                val time = addTaskTime()
                addNewTask(tasksList, priority, date, time)
            }
            "print" -> printTasks(tasksList)
            "edit" -> editTask(tasksList)
            "delete" -> deleteTask(tasksList)
            "end" -> {
                println("Tasklist exiting!")
                saveToJson(taskAdapter, taskJson, tasksList)
                break
            }
            else -> println("The input action is invalid")
        }
    }

}

fun saveToJson(taskAdapter: JsonAdapter<List<Task>>, taskJson: File, tasksList: MutableList<Task>) {
    taskJson.writeText(taskAdapter.toJson(tasksList))
}

fun editTask(taskList: MutableList<Task>) {
    printTasks(taskList)

    if (taskList.isNotEmpty()) {
        while (true) {
            try {
                println("Input the task number (1-${taskList.size}):")
                val taskEdit = readln().toInt()

                if (taskEdit in 1..taskList.size) {
                    while (true) {
                        println("Input a field to edit (priority, date, time, task):")
                        when (readln()) {
                            "priority" -> {
                                val task = editTaskOption(taskList[taskEdit - 1], priority = true)
                                taskList[taskEdit - 1] = task
                                break
                            }
                            "date" -> {
                                val task = editTaskOption(taskList[taskEdit - 1], date = true)
                                taskList[taskEdit - 1] = task
                                break
                            }
                            "time" -> {
                                val task = editTaskOption(taskList[taskEdit - 1], time = true)
                                taskList[taskEdit - 1] = task
                                break
                            }
                            "task" -> {
                                val task = editTaskOption(taskList[taskEdit - 1], taskEdit = true)
                                taskList[taskEdit - 1] = task
                                break
                            }
                            else -> println("Invalid field")
                        }
                    }
                    println("The task is changed")
                    break
                } else println("Invalid task number")

            } catch (_: Exception) {
                println("Invalid task number")
                continue
            }
        }
    }

}

fun editTaskOption(
    task: Task,
    priority: Boolean = false,
    date: Boolean = false,
    time: Boolean = false,
    taskEdit: Boolean = false
): Task {
    var copyDate = task.date
    var copyHour = task.time
    var copyPriority = task.priority
    val tag = task.tag
    val copyTask = task.task

    if (priority) copyPriority = addTaskPriority()
    if (date) copyDate = addTaskDate()
    if (time) copyHour = addTaskTime()

    if (taskEdit) {
        task.task.removeAll(task.task)
        val newTask = addTask()

        return Task(copyPriority, copyDate, tag, copyHour, newTask)
    }

    return Task(copyPriority, copyDate, tag, copyHour, copyTask)
}

fun deleteTask(taskList: MutableList<Task>) {

    printTasks(taskList)

    if (taskList.isNotEmpty()) {
        while (true) {
            try {
                println("Input the task number (1-${taskList.size}):")
                val taskDelete = readln().toInt()
                if (taskDelete in 1..taskList.size) {
                    taskList.removeAt(taskDelete - 1)
                    println("The task is deleted")
                    break
                } else println("Invalid task number")

            } catch (_: Exception) {
                println("Invalid task number")
                continue
            }
        }
    }
}

fun addTaskPriority(): String {
    while (true) {
        println("Input the task priority (C, H, N, L):")
        return when (readln().uppercase()) {
            "C" -> "C"
            "H" -> "H"
            "N" -> "N"
            "L" -> "L"
            else -> continue
        }

    }


}

fun addTaskDate(): String {
    var date: LocalDate

    while (true) {
        return try {
            println("Input the date (yyyy-mm-dd): ")
            val data = readln().split("-").toMutableList()

            data[1] = if (data[1].toInt() < 9 && data[1].length == 1) "0${data[1]}" else data[1]
            data[2] = if (data[2].toInt() < 9 && data[2].length == 1) "0${data[2]}" else data[2]

            date = LocalDate.parse("${data[0]}-${data[1]}-${data[2]}")
            date.toString()
        } catch (_: Exception) {
            println("The input date is invalid")
            continue
        }
    }


}

fun addTaskTime(): String {
    while (true) {
        try {
            println("Input the time (hh:mm):")
            val hour = readln().split(":").toMutableList()

            if (hour[0].toInt() in 0..23 && hour[1].toInt() in 0..59) {
                hour[0] = if (hour[0].toInt() < 10 && hour[0].length == 1) "0${hour[0]}" else hour[0]
                hour[1] = if (hour[1].toInt() < 10 && hour[1].length == 1) "0${hour[1]}" else hour[1]
                return "${hour[0]}:${hour[1]}"
            } else {
                println("The input time is invalid")
            }
        } catch (_: Exception) {
            println("The input time is invalid")
            continue
        }

    }

}

fun addNewTask(taskList: MutableList<Task>, priority: String, date: String, time: String) {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
    val numberOfDays = currentDate.daysUntil(date.toLocalDate())
    val tag = if (numberOfDays == 0) "T" else if (numberOfDays > 0) "I" else "O"

    val task = addTask()

    if (task.isNotEmpty()) taskList.add(Task(priority, date, tag, time, task))
}

private fun addTask(): MutableList<String> {
    val task = mutableListOf<String>()

    println("Input a new task (enter a blank line to end): ")
    while (true) {
        val tas = readln().trimStart()
        if (tas.isNotBlank()) {
            task.add(tas)
        } else if (tas.isBlank() && task.isNotEmpty()) {
            break
        } else {
            println("The task is blank")
            break
        }
    }

    return task
}

fun printTasks(tasksList: MutableList<Task>) {
    if (tasksList.isEmpty()) {
        println("No tasks have been input")
    } else {
        printFirstLine()
        printEndLine()
        for (i in tasksList.indices) {
            printLine("${i + 1}", tasksList[i])
            printEndLine()
        }

    }
}

private fun printFirstLine() {
    printEndLine()
    print("|")
    print(" N  ")
    print("|")
    print("    Date    ")
    print("|")
    print(" Time  ")
    print("|")
    print(" P ")
    print("|")
    print(" D ")
    print("|")
    print("                   Task                     ")
    println("|")
}

private fun printEndLine() {
    println("+----+------------+-------+---+---+--------------------------------------------+")
}

private fun printLine(
    index: String = " ",
    lineDataTimePD: Task
) {
    val (p, data, d, hour, task) = lineDataTimePD

    val priorityColor = getColorPriority(p)
    val duoTagColor = getColorDuoTag(d)

    print("|")
    if (index.toInt() < 10) print(" $index  ") else print(" $index ")
    print("|")
    if (data.isNotEmpty()) print(" $data ") else print(" ".repeat(14))
    print("|")
    if (hour.isNotEmpty()) print(" $hour ") else print(" ".repeat(7))
    print("|")
    if (priorityColor.isNotEmpty()) print(" $priorityColor ") else print(" ".repeat(3))
    print("|")
    if (duoTagColor.isNotEmpty()) print(" $duoTagColor ") else print(" ".repeat(3))
    print("|")
    for (i in 0 until task.size) {
        if (task[i].length < 44 && i == 0) {
            val spacing = 44 - task[i].length
            print(task[i])
            print(" ".repeat(spacing))
            println("|")
        } else if (task[i].length > 44 && i == 0) {
            val taskEndLine = task[i].length - 44
            print(task[i].take(44))
            println("|")
            printSecondLine(task[i].takeLast(taskEndLine))
        } else if (task[i].length > 44 && i > 1) {
            val taskEndLine = task[i].length - 44
            printSecondLine(task[i].take(44))
            printSecondLine(task[i].takeLast(taskEndLine))
        } else {
            printSecondLine(task[i])
        }
    }

}

private fun printSecondLine(
    task: String = ""
) {

    print("|")
    print("    ")
    print("|")
    print(" ".repeat(12))
    print("|")
    print(" ".repeat(7))
    print("|")
    print(" ".repeat(3))
    print("|")
    print(" ".repeat(3))
    print("|")
    val spacing = 44 - task.length
    print(task)
    print(" ".repeat(spacing))
    println("|")

}

fun getColorDuoTag(tag: String): String = when (tag) {
    "I" -> "\u001B[102m \u001B[0m"
    "T" -> "\u001B[103m \u001B[0m"
    "O" -> "\u001B[101m \u001B[0m"
    else -> "\u001B[102m \u001B[0m"
}

fun getColorPriority(p: String): String = when (p.uppercase()) {
    "C" -> "\u001B[101m \u001B[0m"
    "H" -> "\u001B[103m \u001B[0m"
    "N " -> "\u001B[102m \u001B[0m"
    "L" -> "\u001B[104m \u001B[0m"
    else -> "\u001B[102m \u001B[0m"
}

