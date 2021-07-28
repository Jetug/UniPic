package com.example.unipicdev.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import kotlin.math.min

fun showFullImage(file: File, context: Context, imageView: ImageView, animateGifs: Boolean = false){
    val glide = Glide.with(context)
        .load(file)
    if(!animateGifs)
        glide.dontAnimate()
    glide.into(imageView)
}

class ImageCreator {

    private val dataSaver = DataSaver()

    fun getThumbnail(path: String, size: Int): Bitmap? {
        var bitmap = getBitmap(path)
        bitmap = bitmap?.toSquare(size)
        return bitmap
    }


    fun showThumbnail(file: File, context: Context, imageView: ImageView, size: Int, animateGifs: Boolean = false){
        val glide = Glide.with(context)
            .load(file)
            .centerCrop()
            .apply(RequestOptions().override(size, size))
        if(!animateGifs)
            glide.dontAnimate()
        glide.into(imageView)
    }

    fun showFolderThumbnail(files: Array<File>, context: Context, imageView: ImageView, size: Int) {
        if(files.isNotEmpty()) {
            val dirPath: String = files[0].parent
            val sorting: SortingType = getDirSorting(dirPath)
            var media = files

            fun reverse()
            {
                media.reverse()
            }

            when(sorting){
                SortingType.NONE ->{
                    files.sortBy{ it.lastModified() }
                    reverse()
                }
                SortingType.NAME -> {
                    media.sortBy { it.name }
                }
                SortingType.CREATION_DATE -> {
                    media.sortBy {
                        val path = FileSystems.getDefault().getPath(it.absolutePath)
                        val attr = Files.readAttributes(path, BasicFileAttributes::class.java)
                        return@sortBy attr.creationTime()
                    }
                }
                SortingType.MODIFICATION_DATE -> {
                    files.sortBy{ it.lastModified() }
                }
                SortingType.CUSTOM -> {
                    media = dataSaver.getCustomMediaListF(dirPath).toTypedArray()
                }
            }

            for (currentFile in media) {
                if (currentFile.isFile && isMediaFile(currentFile)) {
                    showThumbnail(currentFile, context, imageView, size, false)
                    break
                }
            }
        }
    }

    private fun isMediaFile(file: File): Boolean = supportedExtentions.contains(file.extension)

    fun showFolderThumbnail(file: File, context: Context, imageView: ImageView, size: Int) {
        val files = file.listFiles()
        if(files != null) {
            //showThumbnail(files[0], context, imageView, size, false)
            for (currentFile in files) {
                if (currentFile.isFile && supportedExtentions.contains(currentFile.extension)) {
                    showThumbnail(currentFile, context, imageView, size, false)
                    break
                }
            }
        }
    }

    fun getFolderThumbnail(path: String, size: Int): Bitmap? {
        val folder = File(path)

        var bitmap: Bitmap? = null

        val files = folder.listFiles()
        for (currentFile in files) {
            if (currentFile.isFile && supportedExtentions.contains(currentFile.extension)) {
                val bmOptions = BitmapFactory.Options()
                bitmap = getThumbnail(currentFile.absolutePath, size)
                break
            }
        }
        return bitmap
    }

    fun getBitmap(path: String): Bitmap? {
        var bitmap: Bitmap? = null
        val bmOptions = BitmapFactory.Options()
        bitmap = BitmapFactory.decodeFile(path, bmOptions)
        return bitmap
    }


    private fun Bitmap.toSquare(size: Int): Bitmap?{
        // get the small side of bitmap
        val side = min(width,height)

        val xOffset = (width - side) /2
        val yOffset = (height - side)/2

        val bitmap = Bitmap.createBitmap(
            this,
            xOffset,
            yOffset,
            side,
            side
        )
        return bitmap
    }
}