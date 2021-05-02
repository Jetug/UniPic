package com.example.unipic.models

import kotlinx.coroutines.*
import java.io.File

val supportedExtentions = arrayOf("jpg","jpeg","bmp","png","gif")

class  MediaSearcher {

    private val dataSaver = DataSaver()
    private val initPath = "/storage/emulated/0/"

    fun getDirectories(onFind: (file: File) -> Unit ){
        CoroutineScope(Dispatchers.Default).launch{
            //dataSaver.getSavedDirs(onFind)
            searchDirectories(File(initPath), onFind)
        }
    }

    fun getImageFiles(path: String): ArrayList<File> {
        val dirs = File(path).listFiles()
        val imageList = ArrayList<File>()
        if (dirs != null) {
            for (file in dirs) {
                if (isMediaFile(file)) {
                    imageList.add(file)
                }
            }
        }
        return imageList
    }

    private fun isMediaFile(file:File):Boolean = supportedExtentions.contains(file.extension)

    private fun searchDirectories(directory: File, onFind: (file: File) -> Unit){
        val directories:Array<File>? = directory.listFiles()
        if (directories != null) {
            for (file in directories) {
                if (file.isDirectory) {
                    if (isMediaFolder(file)) {
                        onFind(file)
                        //dataSaver.saveDir(file.absolutePath)
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