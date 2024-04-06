package com.example.todolist.android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class TasksListAdapter(private val context: Context, private val tasks: MutableList<Task>) : BaseAdapter() {

    override fun getCount(): Int = tasks.size

    override fun getItem(position: Int): Task = tasks[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val task = getItem(position)
        viewHolder.taskName.text = task.name
        viewHolder.taskName.isChecked = task.isChecked


        return view
    }

    private class ViewHolder(view: View) {
        val taskName: CheckBox = view.findViewById(R.id.taskCheckBox)
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}
