package com.example.todolist.model


enum class TaskState {
    TODO, LATE, DONE
}
data class Task(
    var id: Int = 0,
    val name: String,
    val date: String? = null,
    val description: String? = null,
    var isChecked: Boolean = false,
    var state: TaskState = TaskState.TODO
)
