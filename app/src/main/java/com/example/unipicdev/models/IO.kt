package com.example.unipicdev.models

import java.io.File
import java.time.LocalDate
import java.util.*


fun deleteFile(file: File){
    file.delete()
}

fun renameFile(file: File, newName: String, args: String): File{
    val dir = file.parent
    val fileName = newName + args + "." + file.extension
    val newFile = File(dir, fileName)

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
    file.renameTo(newUnicFile)
    //newFileNames.add(newUnicFile.nameWithoutExtension)

    return newUnicFile
}

fun changeFileDate(file: File, date: Date){
    file.setLastModified(date.time)
}