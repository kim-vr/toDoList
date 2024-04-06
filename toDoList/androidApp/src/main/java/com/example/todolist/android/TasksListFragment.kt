package com.example.todolist.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class TasksListFragment : Fragment(), AddTaskDialogFragment.TaskDialogListener {

    companion object {
        fun newInstance() = TasksListFragment()
    }

    private lateinit var adapter: TasksListAdapter
    private var tasks = arrayListOf<Task>()

    private val fragmentScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tasks_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TasksListAdapter(requireContext(), tasks)
        val listView = view.findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        val addTaskButton = view.findViewById<FloatingActionButton>(R.id.addTaskButton)
        addTaskButton.setOnClickListener {
            val addTaskDialog = AddTaskDialogFragment().apply {
                listener = this@TasksListFragment
            }
            addTaskDialog.show(parentFragmentManager, "AddTaskDialogFragment")
        }

        loadTasks()
    }

    private fun loadTasks() {
        fragmentScope.launch {
            withContext(Dispatchers.IO) {
                val dbHandler = DatabaseHandler(requireContext())
                val newTasks = dbHandler.getAllTasks()
                tasks.clear()
                tasks.addAll(newTasks)
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onTaskAdded(taskName: String, taskDate: String?, taskDescription: String?) {
        val newTask = Task(name = taskName, date = taskDate, description = taskDescription, isChecked = false)
        fragmentScope.launch {
            withContext(Dispatchers.IO) {
                val dbHandler = DatabaseHandler(requireContext())
                dbHandler.insertTask(newTask)
                loadTasks()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentScope.cancel()
    }
}
