package com.example.unipicdev.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.unipicdev.R
import com.example.unipicdev.models.renameFile
import java.io.File


class PropertiesDialog(val file: File) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        return alertDialogBuilder
            .setTitle("Переименование")
            .setMessage(file.absolutePath)
            .setView(R.layout.dialog_properties)
            .setPositiveButton("Ок", ::onPositiveButtonClick)
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }
            .create()
    }

    private fun onPositiveButtonClick(dialog: DialogInterface, id: Int){

    }
}