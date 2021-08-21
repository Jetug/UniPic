package com.example.unipicdev.models

import android.content.Context
import android.util.Log
import com.example.unipicdev.appContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.measureTimeMillis

// "/storage/emulated/0/"
// "/storage/7A5A-1CF6/"
fun isHidden(dir: File):Boolean {
    val files = dir.listFiles()
    if(startsWithDot(dir))
        return true
    if (files != null) {
        for (file in files) {
            if (file.name == ".nomedia")
                return true
        }
    }
    return false
}

fun startsWithDot(dirName: String): Boolean = dirName[0] == '.'
fun startsWithDot(file: File): Boolean = startsWithDot(file.name)

class DirectorySearcher(val context: Context) {
    private val dataSaver = DataSaver()
    private var dirList = mutableListOf<File>()
    private var savedDirectories: ArrayList<File> = ArrayList()
    private var searchJob: Job? = null
    private val notObservableDirs = arrayOf<String>(File("/storage/emulated/0/").absolutePath)

    init {
        initDirList()
    }

    fun getDirectories(onFind: (file: FolderModel) -> Unit ){
        searchJob = CoroutineScope(Dispatchers.Default).launch{
            val time = measureTimeMillis {
                showSavedDirs(onFind)
            }
            Log.d("My", "showSavedDirs $time ms")

            dirList.forEach{
                searchDirectories(it, onFind)
            }
        }
    }

    fun stopSearching(){
        searchJob?.cancel()
    }

    private fun showSavedDirs(onFind: (file: FolderModel) -> Unit){
        savedDirectories = dataSaver.getSavedDirs()
        for(dir in savedDirectories){
            if(dir.exists() && dir.containsMediaFiles())
                onFind(FolderModel(dir))
        }
    }

    private fun searchDirectories(directory: File, onFind: (file: FolderModel) -> Unit){
        val directories:Array<File>? = directory.listFiles()
        if (directories != null) {
            for (file in directories) {
                if (file.isDirectory) {
                    if (isMediaDirectory(file) && !savedDirectories.contains(file) && isObservableDir(file)  /*&& !startsWithDot(file.name)*/) {
                        onFind(FolderModel(file))
                        dataSaver.saveDir(file.absolutePath)
                    }
                    searchDirectories(file, onFind)
                }
            }
        }
    }

    private fun isObservableDir(file: File): Boolean{
        return (!notObservableDirs.contains(file.absolutePath))
    }

    private fun initDirList(){
        val dirs = appContext.getExternalFilesDirs(null)
        dirs.forEach {
            val parent = it.getParentFile(4)
            dirList.add(parent)
        }
    }

    private fun isMediaDirectory(folder: File): Boolean{
        val files:Array<File>? = folder.listFiles()
        if (files != null){
            for (file in files){
                if (file.isFile && file.isMediaFile()){
                    return true
                }
            }
        }
        return false
    }
}