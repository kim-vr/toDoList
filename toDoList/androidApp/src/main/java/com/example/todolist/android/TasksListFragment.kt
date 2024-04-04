package com.example.todolist.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksListFragment : Fragment() {

    companion object {
        fun newInstance() = TasksListFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tasks_list_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Bouton d'ajout de tâches et sa gestion
        super.onViewCreated(view, savedInstanceState)
        val addTask = view.findViewById<FloatingActionButton>(R.id.addTaskButton)
        addTask.setOnClickListener {
            val addTaskDialog = AddTaskDialogFragment()
            addTaskDialog.show(parentFragmentManager, "AddTaskDialogFragment")
        }
    }

}