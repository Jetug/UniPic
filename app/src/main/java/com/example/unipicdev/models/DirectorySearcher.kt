package com.example.unipicdev.models

import android.content.Context
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.core.content.ContextCompat.getObbDirs
import com.example.unipicdev.appContext
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

class DirectorySearcher(val context: Context) {
    private val dataSaver = DataSaver()

    private val initPath = Environment.getExternalStorageDirectory().absolutePath// "/storage/emulated/0/"
    private val sdPath = "/storage/7A5A-1CF6/"

    private var dirList = mutableListOf<File>()

    private var savedDirectories: ArrayList<File> = ArrayList()
    private var searchJob: Job? = null

    init {
        initDirList()
    }

    fun getDirectories(onFind: (file: FolderModel) -> Unit ){
        searchJob = CoroutineScope(Dispatchers.Default).launch{
            showSavedDirs(onFind)

            dirList.forEach{
                searchDirectories(it, onFind)
            }
        }
    }

    private fun showSavedDirs(onFind: (file: FolderModel) -> Unit){
        savedDirectories = dataSaver.getSavedDirs()
        for(dir in savedDirectories){
            if(dir.exists())
                onFind(FolderModel(dir))
        }
    }

    fun stopSearching(){
        searchJob?.cancel()
    }

    private fun searchDirectories(directory: File, onFind: (file: FolderModel) -> Unit){
        val directories:Array<File>? = directory.listFiles()
        if (directories != null) {
            for (file in directories) {
                if (file.isDirectory) {
                    if (isMediaDirectory(file) && !savedDirectories.contains(file) && !startsWithDoc(file.name)) {
                        onFind(FolderModel(file))
                        dataSaver.saveDir(file.absolutePath)
                    }
                    searchDirectories(file, onFind)
                }
            }
        }
    }

    private fun initDirList(){
        val dirs = appContext.getExternalFilesDirs(null)
        dirs.forEach {
            dirList.add(getParentFile(it, 4))
        }
    }

    private fun getParentFile(file: File, downTo: Int): File {
        var parent: File = file
        for (i in 1..downTo){
            if(parent.parentFile != null){
                parent = parent.parentFile
            }
        }
        return parent
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