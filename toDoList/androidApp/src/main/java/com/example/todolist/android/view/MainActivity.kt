package com.example.todolist.android.view

import com.example.todolist.android.notifications.TaskReminderManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.android.R

class MainActivity : AppCompatActivity() {

    lateinit var tasksListAdapter: TasksListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TasksListFragment.newInstance())
                .commitNow()
        }

        TaskReminderManager.createNotificationChannel(this)
    }
}



