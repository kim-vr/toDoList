package com.example.todolist.android

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskDialogFragment : DialogFragment() {

    interface TaskDialogListener {
        fun onTaskAdded(taskName: String, taskDate: String?, taskDescription: String?)
    }

    var listener: TaskDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.add_task_layout, null)

        val nameInput = view.findViewById<EditText>(R.id.task_input)
        val dateInput = view.findViewById<EditText>(R.id.date_input)
        val descriptionInput = view.findViewById<EditText>(R.id.description_input)

        //Permet d'ouvrir un DatePickerDialog
        dateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                //Formate et affiche la date sélectionnée dans le champ de texte de date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateInput.setText(dateFormat.format(selectedDate.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        builder.setView(view)
            .setTitle("Ajouter une tâche")
            .setPositiveButton("Ajouter") { dialog, id ->
                val taskNameText = nameInput.text.toString() // Convertit le contenu de EditText en String
                val taskDateText = dateInput.text.toString().let { if(it.isEmpty()) null else it } // Convertit et vérifie si le champ est vide
                val taskDescriptionText = descriptionInput.text.toString().let { if(it.isEmpty()) null else it } // Idem pour la description
                listener?.onTaskAdded(taskNameText, taskDateText, taskDescriptionText)
            }
            .setNegativeButton("Annuler") { dialog, id ->
                dialog.cancel()
            }
        return builder.create()
    }


}