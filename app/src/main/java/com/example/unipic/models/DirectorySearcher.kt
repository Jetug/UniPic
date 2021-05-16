package com.example.unipic.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

fun isHidden(dir: File):Boolean {
    val files = dir.listFiles()
    if (files != null) {
        for (file in files) {
            if (file.name == ".nomedia")
                return true
        }
    }
    return false
}

class DirectorySearcher {
    private val dataSaver = DataSaver()
    private val initPath = "/storage/emulated/0/"
    private var savedDirectories: ArrayList<File> = ArrayList()
    private var searchjob: Job? = null


    init {
        //this.showHidden = showHidden
    }

    fun getDirectories(onFind: (file: File) -> Unit ){
        searchjob = CoroutineScope(Dispatchers.Default).launch{
            savedDirectories = dataSaver.getSavedDirs()
            for(dir in savedDirectories){
                onFind(dir)
            }
            searchDirectories(File(initPath), onFind)
        }
    }

    fun stopSearching(){
        searchjob?.cancel()
    }

    fun getNotHiddenDirectories(){

    }

    private fun searchDirectories(directory: File, onFind: (file: File) -> Unit){
        val directories:Array<File>? = directory.listFiles()
        if (directories != null) {
            for (file in directories) {
                if (file.isDirectory) {
                    if (isMediaDirectory(file) && !savedDirectories.contains(file)) {
                        onFind(file)
                        dataSaver.saveDir(file.absolutePath)
                    }
                    searchDirectories(file, onFind)
                }
            }
        }
    }

    private fun isMediaDirectory(folder: File): Boolean{
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