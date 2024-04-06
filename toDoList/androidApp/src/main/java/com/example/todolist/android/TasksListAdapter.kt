package com.example.todolist.android

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TasksListAdapter(private val context: Context, private var tasks: MutableList<Task>) : BaseAdapter() {

    override fun getCount(): Int = tasks.size

    override fun getItem(position: Int): Task = tasks[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder", "SetTextI18n", "UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false).also {
            viewHolder = ViewHolder(it)
        }

        val task = getItem(position)

        viewHolder.taskName.apply {
            setOnCheckedChangeListener(null)
            text = task.name
            isChecked = task.isChecked
            setOnCheckedChangeListener { _, isChecked ->
                task.isChecked = isChecked
                updateTaskState(task)
                notifyDataSetChanged()
            }
        }

        viewHolder.taskDate.visibility = if (task.date.isNullOrEmpty()) View.GONE else View.VISIBLE
        viewHolder.taskDate.text = "Date limite : ${task.date}"

        viewHolder.taskDescription.visibility = if (task.description.isNullOrEmpty()) View.GONE else View.VISIBLE
        viewHolder.taskDescription.text = "Description : ${task.description}"

        updateButtonState(viewHolder.taskState, task)

        viewHolder.deleteTask.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //Supprime dans la base de données
                val dbHandler = DatabaseHandler(context)
                dbHandler.deleteTask(task.id)

                withContext(Dispatchers.Main) {
                    tasks.removeAt(position)
                    notifyDataSetChanged()
                }
            }
        }

        return view
    }

    private fun updateTaskState(task: Task) {
        task.state = when {
            task.isChecked -> TaskState.DONE
            !task.date.isNullOrEmpty() && LocalDate.parse(task.date).isBefore(LocalDate.now()) -> TaskState.LATE
            else -> TaskState.TODO
        }
    }

    private fun updateButtonState(button: Button, task: Task) {
        button.text = when (task.state) {
            TaskState.TODO -> "À faire"
            TaskState.LATE -> "En retard"
            TaskState.DONE -> "Réalisée"
        }

        val color = when (task.state) {
            TaskState.TODO -> android.R.color.holo_blue_light
            TaskState.LATE -> android.R.color.holo_red_light
            TaskState.DONE -> android.R.color.holo_green_light
        }
        button.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    class ViewHolder(view: View) {
        val taskName: CheckBox = view.findViewById(R.id.taskCheckBox)
        val taskDate: TextView = view.findViewById(R.id.taskDate)
        val taskDescription: TextView = view.findViewById(R.id.taskDescription)
        val taskState: Button = view.findViewById(R.id.taskStateButton)
        val deleteTask: ImageButton = view.findViewById(R.id.deleteTaskButton)
    }

    fun updateTasks(newTasks: MutableList<Task>) {
        this.tasks = newTasks
        notifyDataSetChanged()
    }
}
