package com.example.unipic.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

class  MediaSearcher {
    val supportedExtentions = arrayOf("jpg","jpeg","bmp","png","gif")

    fun getFolderFiles(): ArrayList<File> {
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

    fun isMediaFile(file:File):Boolean = supportedExtentions.contains(file.extension)

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

    fun getThumbnail(path: String): Bitmap? {
        var bitmap: Bitmap? = null
        val bmOptions = BitmapFactory.Options()
        bitmap = BitmapFactory.decodeFile(path, bmOptions)
        return bitmap
    }

     fun getFolderThumbnail(path: String): Bitmap? {
        val folder = File(path)

        val folderList = ArrayList<File>()
        folderList.add(folder);

        var bitmap: Bitmap? = null

        val files = folder.listFiles()
        for (currentFile in files) {
            if (currentFile.isFile && supportedExtentions.contains(currentFile.extension)) {
                val bmOptions = BitmapFactory.Options()
                bitmap = getThumbnail(currentFile.absolutePath)
                break
            }
        }
        return bitmap
    }
}