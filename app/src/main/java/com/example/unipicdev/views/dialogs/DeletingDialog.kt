package com.example.unipicdev.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.unipicdev.R
import com.example.unipicdev.models.ThumbnailModel
import com.example.unipicdev.models.changeFileDate
import com.example.unipicdev.models.deleteFile
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period

class DeletingDialog(private val onComplete: () -> Unit ) : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(activity)

        return alertDialogBuilder
            .setTitle("Подтвердите удаление")
            .setPositiveButton("Ок", ::onPositiveButtonClick)
            .setNegativeButton("Отмена") { dialog, id ->
                dialog.cancel()
            }
            .create()
    }

    private fun onPositiveButtonClick(dialog: DialogInterface, id: Int){
        onComplete()
    }
}