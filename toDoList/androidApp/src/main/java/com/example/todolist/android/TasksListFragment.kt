package com.example.todolist.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import java.time.LocalDate

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

        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
        // Configurez ici vos onglets, par exemple:
        tabLayout.apply {
            addTab(tabLayout.newTab().setText("Toutes"))
            addTab(tabLayout.newTab().setText("Réalisées"))
            addTab(tabLayout.newTab().setText("À faire"))
            addTab(tabLayout.newTab().setText("En retard"))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        val state = when (it.position) {
                            1 -> TaskState.DONE
                            2 -> TaskState.TODO
                            3 -> TaskState.LATE
                            else -> null
                        }
                        filterTasksByState(state)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // Ne rien faire
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // Ne rien faire
                }
            })

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
            updateTaskStates()
            adapter.updateTasks(tasks)
        }
    }

    private fun updateTaskStates() {
        val dbHandler = DatabaseHandler(requireContext())
        val today = LocalDate.now()
        tasks.forEach { task ->
            val taskDate = task.date?.let { LocalDate.parse(it) }
            if (!task.isChecked && taskDate != null && taskDate.isBefore(today)) {
                task.state = TaskState.LATE
                dbHandler.updateTask(task) // Assurez-vous que cette méthode met à jour l'état dans la DB
            }
        }
    }

    private fun filterTasksByState(state: TaskState?) {
        val filteredTasks = when (state) {
            TaskState.DONE -> tasks.filter { it.state == TaskState.DONE }
            TaskState.TODO -> tasks.filter { it.state == TaskState.TODO }
            TaskState.LATE -> tasks.filter { it.state == TaskState.LATE }
            else -> tasks
        }
        adapter.updateTasks(filteredTasks.toMutableList())
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
