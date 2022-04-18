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
    for ((i, value) in tasksList.withIndex()) {
        println("$i - ${value.task}")
    }
}