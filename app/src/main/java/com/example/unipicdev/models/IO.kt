package com.example.unipicdev.models

import com.example.unipicdev.supportedExtension
import org.joda.time.DateTime
import java.io.File


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
    return newUnicFile
}

fun changeFileDate(file: File, date: DateTime){
    file.setLastModified(date.toDate().time)
}

fun File.getParentFile(downTo: Int): File {
    var parent: File = this
    for (i in 1..downTo){
        val buff = parent.parentFile
        if(buff != null){
            parent = buff
        }
    }
    return parent
}

fun File.isMediaFile() = supportedExtension.contains(this.extension)

fun File.getMediaFiles(): Array<File>{
    val medias = mutableListOf<File>()
    if(this.isDirectory) {
        val files = this.listFiles()
        files?.forEach {
            if (it.isMediaFile()) {
                medias.add(it)
            }
        }
    }
    return medias.toTypedArray()
}

fun File.containsMediaFiles(): Boolean {
    listFiles()?.forEach {
        if (it.isMediaFile()) return true
    }
    return false
}