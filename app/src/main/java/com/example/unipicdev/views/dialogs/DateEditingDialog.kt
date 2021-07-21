package com.example.unipicdev.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.unipicdev.R
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


class DateEditingDialog(onComplite: () -> Unit): DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_date_editing, null)

       // view.findViewById<EditText>()

        val alertDialogBuilder = AlertDialog.Builder(activity)
        return alertDialogBuilder
            .setTitle("Изменить дату")
            .setView(view)
            .setPositiveButton("Ок", ::onPositiveButtonClick)
            .setNegativeButton("Отмена") { dialog, id ->
                dialog.cancel()
            }
            //.setAdapter()
            .create()
    }

    private fun onPositiveButtonClick(dialog: DialogInterface, id: Int){
        val f = dialog as Dialog

        var initDate = LocalDateTime.of(2020,1,1,0,0,0)

        var stepDate = LocalDateTime.of(0,0,0,0,0,1)



        //onComplite(LocalDate(2020,1,1))

        //val dateET = f.findViewById<EditText>(R.id.editTextDate)

        //val text = dateET.text

    }
}


