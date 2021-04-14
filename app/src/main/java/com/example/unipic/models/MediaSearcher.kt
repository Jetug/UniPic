package com.example.unipic.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import kotlin.math.min

val supportedExtentions = arrayOf("jpg","jpeg","bmp","png","gif")

class  MediaSearcher {
    fun getDirectories(): ArrayList<File> {
        val path = "/storage/emulated/0/"

        return scanFolder(File(path))
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

    fun scanFolder(folder: File): ArrayList<File> {
        val result = ArrayList<File>()

        for (file in folder.listFiles()){
            if(file.isDirectory){
                if(isMediaFolder(file)){
                    result.add(file)
                }
                val recRes = scanFolder(file)
                result.addAll(recRes);
            }
        }
        return result
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