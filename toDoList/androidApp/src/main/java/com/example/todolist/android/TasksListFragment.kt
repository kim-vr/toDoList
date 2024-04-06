package com.example.todolist.android

import TaskReminderManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import java.time.LocalDate

interface AnimationCallback {
    fun showCheckAnimation()
}
class TasksListFragment : Fragment(), AddTaskDialogFragment.TaskDialogListener, AnimationCallback{

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

        adapter = TasksListAdapter(requireContext(), tasks, this@TasksListFragment)
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
                    //Ne rien faire
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    //Ne rien faire
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
                dbHandler.updateTask(task)
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
        TaskReminderManager.setTaskReminder(requireContext(), newTask)
    }

    override fun showCheckAnimation() {
        val animationView = view?.findViewById<ImageView>(R.id.animationView)
        animationView?.let {
            it.visibility = View.VISIBLE
            val drawable = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.animation)
            it.setImageDrawable(drawable)
            (drawable as? Animatable)?.start()

            it.postDelayed({ it.visibility = View.GONE }, 1000)  // Supposant que votre animation dure 1000ms
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragmentScope.cancel()
    }
}
