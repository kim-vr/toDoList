package com.example.todolist.android

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater

class AddTaskDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.add_task_layout, null)
        builder.setView(view)
            .setTitle("Ajouter une tâche")
            .setPositiveButton("Ajouter") { dialog, id ->
                //TODO
            }
            .setNegativeButton("Annuler") { dialog, id ->
                dialog.cancel()
            }
        return builder.create()
    }
}