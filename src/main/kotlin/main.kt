import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
            //"edit" -> editTask(tasksList)
            //"delete" -> deleteTask(tasksList)
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

fun addTaskPriority(): String {
    return "C"
}

fun addTaskDate(): String {
    return "2022-04-18"
}

fun addTaskTime(): String {
    return "18:00"
}


fun addNewTask(taskList: MutableList<Task>, priority: String, date: String, time: String) {
    val tag = "T"
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

    taskList.add(Task(priority, date, tag, time, task))

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