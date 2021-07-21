package com.example.unipicdev.views.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.unipicdev.R
import com.example.unipicdev.models.renameFile
import java.io.File


class MediaRenamingDialog(var files: List<File>,
                          val onRename: (position: Int, file: File) -> Unit,
                          val onComplete:(newFiles: MutableList<File>)->Unit ) : DialogFragment() {
    private var newName: String = "Test"
    private var newFileNames: MutableList<File> = mutableListOf()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        return alertDialogBuilder
            .setTitle("Переименование")
            .setView(R.layout.dialog_media_renaming)
            .setPositiveButton("Ок", ::onPositiveButtonClick)
            .setNegativeButton("Отмена") { dialog, id ->
                    dialog.cancel()
                }
            .create()
    }

    private fun onPositiveButtonClick(dialog: DialogInterface, id: Int){
        val f = dialog as Dialog
        val renameEditText = f.findViewById<EditText>(R.id.renameET)
        newName = renameEditText.text.toString()

        if(files.size == 1){
            val file = files[0]
            val newFile = renameFile(file, "", newName)
            //onRename(0, newFile)
            newFileNames.add(newFile)
        }
        else {
            for ((i, file) in files.withIndex()) {
                val num = i + 1
                val newFile = renameFile(file, newName, num.toString())
                //onRename(0, newFile)
                newFileNames.add(newFile)
            }
            //dialog.cancel()
        }
        onComplete(newFileNames)
    }

//    interface OnCompleteListener {
//        fun onComplete(newFiles: MutableList<File>)
//    }
//
//    private var mListener: OnCompleteListener? = null

}