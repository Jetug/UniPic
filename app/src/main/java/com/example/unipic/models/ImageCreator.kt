package com.example.unipic.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import kotlin.math.min

fun showFullImage(file: File, context: Context, imageView: ImageView, animateGifs: Boolean = false){
    val glide = Glide.with(context)
        .load(file)
    if(!animateGifs)
        glide.dontAnimate()
    glide.into(imageView)
}

class ImageCreator {

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

    fun showFolderThumbnail(file: File, context: Context, imageView: ImageView, size: Int) {
        val files = file.listFiles()
        if(files != null) {
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