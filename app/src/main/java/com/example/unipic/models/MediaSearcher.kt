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
        val dir = File(path)
        val imageList = ArrayList<File>()
        for (file in dir.listFiles()){
            if(isMediaFile(file)){
                imageList.add(file)
            }
        }
        return imageList
    }

    private fun isMediaFile(file:File):Boolean = supportedExtentions.contains(file.extension)

    private fun searchDirectories(folder: File, onFind: (file: File) -> Unit){
        //val result = ArrayList<File>()

        for (file in folder.listFiles()){
            if(file.isDirectory){
                if(isMediaFolder(file)){
                    //result.add(file)
                    onFind(file)
                    dataSaver.saveDir(file.absolutePath)
                }
                searchDirectories(file, onFind)
                ///result.addAll(recRes);
            }
        }
        //return result
    }

    private fun isMediaFolder(folder: File): Boolean{
        val files:Array<File> = folder.listFiles()
        for (file in files){
            if (file.isFile && isMediaFile(file)){
                return true
            }
        }
        return false
    }


}