package com.example.todolist.android

data class Task(
    var id: Int = 0,
    val name: String,
    val date: String? = null,
    val description: String? = null,
    val isChecked: Boolean = false
)
