package com.example.unipic.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

val supportedExtentions = arrayOf("jpg","jpeg","bmp","png","gif")

fun isMediaFile(file:File):Boolean = supportedExtentions.contains(file.extension)

class  MediaSearcher {
    fun showImageFiles(path: String, onFind: (file: File) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val dirs = File(path).listFiles()
            if (dirs != null) {
                for (file in dirs) {
                    if (isMediaFile(file)) {
                        onFind(file)
                    }
                }
            }
        }
    }

    fun getImageFiles(path: String): MutableList<File> {

        val result = mutableListOf<File>()
            val dirs = File(path).listFiles()
            if (dirs != null) {
                for (file in dirs) {
                    if (isMediaFile(file)) {
                        result.add(file)
                    }
                }
            }
        return result
    }
}