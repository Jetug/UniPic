package com.example.unipicdev.views.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.unipicdev.R
import kotlinx.android.synthetic.main.dialog_media_renaming.*
import java.io.File


class MediaRenamingDialog(var files: List<File>) : DialogFragment() {
    private var newName: String = "Test"
    private var newFileNames: MutableList<String> = mutableListOf()



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        return alertDialogBuilder
            .setTitle("Диалоговое окно")
            .setMessage("Для закрытия окна нажмите ОК")
            .setView(R.layout.dialog_media_renaming)
            .setPositiveButton("Ок", ::onPositiveButtonClick)
            //.setPositiveButton("Ок", DialogInterface.OnClickListener { dialog, id -> })
            .setNegativeButton("Отмена") { dialog, id ->
                    dialog.cancel()
                }
            .create()
    }

    private fun onPositiveButtonClick(dialog: DialogInterface, id: Int){
        val f = dialog as Dialog
        val renameEditText = f.findViewById<EditText>(R.id.renameET)
        newName = renameEditText.text.toString()

        //newName = renameET.text.toString()
        //var t = newName
        if(files.size == 1){
            val file = files[0]
            doWork(file, "")
        }
        else {
            for ((i, file) in files.withIndex()) {
                val num = i + 1
                doWork(file, num.toString())
            }
            dialog.cancel()
        }

        val intent = Intent()
        intent.putExtra("newFileNames", arrayOf(newFileNames))
    }

    private fun rename(oldFile: File, newFile: File){
        var newUnicFile = newFile
        if(newFile.exists()){
            var i = 0
            val name = newFile.nameWithoutExtension
            val dir = newFile.parent
            var fileName = ""
            while (true){
                i++
                fileName = name + "~" + i + "." + newFile.extension
                val buffFile = File(dir, fileName)
                if (!buffFile.exists()){
                    newUnicFile = buffFile
                    break
                }
            }
        }
        oldFile.renameTo(newUnicFile)
        newFileNames.add(newUnicFile.nameWithoutExtension)
    }

    private fun doWork(file: File, args: String){
        val dir = file.parent
        val fileName = newName + args + "." + file.extension
        val newFile = File(dir, fileName)
        rename(file, newFile)
    }
}