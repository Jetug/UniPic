package com.example.unipic.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

// "/storage/emulated/0/"
// "/storage/7A5A-1CF6/"
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

fun startsWithDoc(path: String): Boolean = path[0] == '.'

class DirectorySearcher {
    private val dataSaver = DataSaver()

    private val initPath = "/storage/emulated/0/"
    private val sdPath = "/storage/7A5A-1CF6/"
    private var savedDirectories: ArrayList<File> = ArrayList()
    private var searchJob: Job? = null

    init {
        //this.showHidden = showHidden
    }

    fun getDirectories(onFind: (file: File) -> Unit ){
        searchJob = CoroutineScope(Dispatchers.Default).launch{
            showsSavedDirs(onFind)
            searchDirectories(File(initPath), onFind)
            searchDirectories(File(sdPath), onFind)
        }
    }

    private fun showsSavedDirs(onFind: (file: File) -> Unit){
        savedDirectories = dataSaver.getSavedDirs()
        for(dir in savedDirectories){
            if(dir.exists())
                onFind(dir)
        }
    }

    fun stopSearching(){
        searchJob?.cancel()
    }

    fun getNotHiddenDirectories(){

    }

    private fun searchDirectories(directory: File, onFind: (file: File) -> Unit){
        val directories:Array<File>? = directory.listFiles()
        if (directories != null) {
            for (file in directories) {
                if (file.isDirectory) {
                    if (isMediaDirectory(file) && !savedDirectories.contains(file) && !startsWithDoc(file.name)) {
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