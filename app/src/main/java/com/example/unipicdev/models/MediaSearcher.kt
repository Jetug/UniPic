package com.example.unipicdev.models

import com.example.unipicdev.supportedExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

//fun isMediaFile(file:File):Boolean = supportedExtension.contains(file.extension)

class  MediaSearcher {
    fun showImageFiles(path: String, onFind: (file: File) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val dirs = File(path).listFiles()
            if (dirs != null) {
                for (file in dirs) {
                    if (file.isMediaFile()) {
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
                    if (file.isMediaFile()) {
                        result.add(file)
                    }
                }
            }
        return result
    }
}