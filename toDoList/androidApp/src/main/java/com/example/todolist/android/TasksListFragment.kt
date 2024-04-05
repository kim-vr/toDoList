package com.example.todolist.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksListFragment : Fragment(), AddTaskDialogFragment.TaskDialogListener {

    private lateinit var adapter: TasksListAdapter
    private val tasks = arrayListOf<Task>()

    companion object {
        fun newInstance() = TasksListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tasks_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addTaskButton = view.findViewById<FloatingActionButton>(R.id.addTaskButton)
        addTaskButton.setOnClickListener {
            val addTaskDialog = AddTaskDialogFragment()
            addTaskDialog.listener = this
            addTaskDialog.show(parentFragmentManager, "AddTaskDialogFragment")
        }

        adapter = TasksListAdapter(requireContext(), tasks)
        val listView = view.findViewById<ListView>(R.id.listView)
        listView.adapter = adapter
    }

    override fun onTaskAdded(taskName: String, taskDate: String?, taskDescription: String?) {
        val newTask = Task(taskName, taskDate, taskDescription)
        adapter.addTask(newTask)
    }
}
