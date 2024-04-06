package com.example.todolist.android.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import com.example.todolist.android.R
import com.example.todolist.model.Task

class TaskAdapter(private val context: Context, private val tasks: ArrayList<Task>) : BaseAdapter() {

    private class ViewHolder(row: View?) {
        var taskCheckBox: CheckBox? = null

        init {
            this.taskCheckBox = row?.findViewById(R.id.taskCheckBox)
        }
    }

    override fun getCount(): Int = tasks.size

    override fun getItem(position: Int): Any = tasks[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        val viewHolder: ViewHolder

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.task_layout, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val task = getItem(position) as Task
        viewHolder.taskCheckBox?.text = task.name

        return view!!
    }
}
