package com.example.unipic.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

val supportedExtentions = arrayOf("jpg","jpeg","bmp","png","gif")

class  MediaSearcher {

    private val dataSaver = DataSaver()
    private val initPath = "/storage/emulated/0/"
    private var savedDirectories: ArrayList<File> = ArrayList()

    fun getDirectories(onFind: (file: File) -> Unit ){
        CoroutineScope(Dispatchers.Default).launch{
            //dataSaver.getSavedDirs(onFind)
            savedDirectories = dataSaver.getSavedDirs()
            for(dir in savedDirectories){
                onFind(dir)
            }
            searchDirectories(File(initPath), onFind)
        }
    }

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

    private fun isMediaFile(file:File):Boolean = supportedExtentions.contains(file.extension)

    private fun searchDirectories(directory: File, onFind: (file: File) -> Unit){
        val directories:Array<File>? = directory.listFiles()
        if (directories != null) {
            for (file in directories) {
                if (file.isDirectory) {
                    if (isMediaFolder(file) && !savedDirectories.contains(file)) {
                        onFind(file)
                            dataSaver.saveDir(file.absolutePath)
                    }
                    searchDirectories(file, onFind)
                }
            }
        }
    }

    private fun isMediaFolder(folder: File): Boolean{
        val files:Array<File>? = folder.listFiles()
        if (files != null){
            for (file in files){
                if (file.isFile && isMediaFile(file)){
                    return true
                }
            }
        }
        return false
    }
}