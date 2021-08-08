package com.example.unipicdev.models

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.unipicdev.getNotNoneSortingOrder
import com.example.unipicdev.getNotNoneSortingType
import com.example.unipicdev.models.room.*
import com.example.unipicdev.models.room.DatabaseApi.getMediaSorting
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import kotlin.math.min
import kotlin.system.measureTimeMillis

@SuppressLint("CheckResult")
fun showFullImage(file: File, context: Context, imageView: ImageView, animateGifs: Boolean = false){
    val glide = Glide.with(context)
        .load(file)
    if(!animateGifs)
        glide.dontAnimate()
    glide.into(imageView)
}

//Pair<SortingType, Order>
//private val sortingMap: MutableMap<String, Sorting> = mutableMapOf()

class ImageFactory {
    data class SortingSave(
        var byName: Pair<String?, String?>? = null,
        var byCreationTime: Pair<String?, String?>? = null,
        var byModificationTime: Pair<String?, String?>? = null,
        var by: Pair<String?, String?>? = null

    )

    data class ThumbnailSave(
        val path: String,
        val sortingType: SortingType,
        val sortingOrder: Order,
    )

    private val dataSaver = DataSaver()
    private val thumbnails = mutableMapOf<String, ThumbnailSave>()

    fun getThumbnail(path: String, size: Int): Bitmap? {
        var bitmap = getBitmap(path)
        bitmap = bitmap?.toSquare(size)
        return bitmap
    }

    @SuppressLint("CheckResult")
    fun showThumbnail(file: File, context: Context, imageView: ImageView, size: Int, animateGifs: Boolean = false){
        CoroutineScope(Dispatchers.Main).launch{
            val glide = Glide.with(context)
                .load(file)
                .centerCrop()
                .apply(RequestOptions().override(size, size))
            if(!animateGifs)
                glide.dontAnimate()
            glide.into(imageView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showFolderThumbnail(files: Array<ThumbnailModel>, context: Context, imageView: ImageView, size: Int) {
        if(files.isNotEmpty()) {
            val time = measureTimeMillis {
                val dirPath: String = files[0].file.parent
                var media = files
                val pair: Pair<SortingType, Order>
                val time = measureTimeMillis {
                    pair = getMediaSorting(dirPath)
                }
                Log.d("My", "pair $time ms")

                val sorting: SortingType = getNotNoneSortingType(pair.first)
                val order: Order = getNotNoneSortingOrder(pair.second)
                var thumbnailFile: File = File("")

                val time2 = measureTimeMillis {
                    val save = thumbnails[dirPath]

                    if(thumbnails.contains(dirPath) && save!!.sortingType == sorting && save.sortingOrder == order){
                        thumbnailFile = File(save.path)
                    }
                    else {
                        media = sortMedias(media, sorting, order) //sort(media, sorting, order)

                        for (currentItem in media) {
                            val currentFile = currentItem.file
                            if (currentFile.isFile && isMediaFile(currentFile)) {
                                thumbnailFile = currentFile
                                thumbnails[dirPath] = ThumbnailSave(thumbnailFile.absolutePath, sorting, order)
                                break
                            }
                        }
                    }
                }
                Log.d("My", "when $time2 ms")

                showThumbnail(thumbnailFile, context, imageView, size, false)
            }
            Log.d("My", "showFolderThumbnail $time ms | thumbnails: ${thumbnails.size}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
//    private fun sort(list:Array<File>, sortingType: SortingType, sortingOrder: Order): Array<File>{
//        var media = list
//
//        fun reverse(){if(sortingOrder == Order.DESCENDING) media.reverse()}
//
//        when (sortingType) {
//            SortingType.NONE -> {
////                media.sortBy { it.lastModified() }
////                reverse()
//            }
//            SortingType.NAME -> {
//                media.sortBy { it.name }
//                reverse()
//            }
//            SortingType.CREATION_DATE -> {
//                media.sortBy {
//                    val path = FileSystems.getDefault().getPath(it.absolutePath)
//                    val attr = Files.readAttributes(path, BasicFileAttributes::class.java)
//                    return@sortBy attr.creationTime()
//                }
//                reverse()
//            }
//            SortingType.MODIFICATION_DATE -> {
//                media.sortBy { it.lastModified() }
//                reverse()
//            }
//            SortingType.CUSTOM -> {
//                media = dataSaver.getCustomMediaListF(media[0].parent).toTypedArray()
//            }
//        }
//
//        return media
//    }

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

    private fun isMediaFile(file: File): Boolean = supportedExtentions.contains(file.extension)

    private fun getBitmap(path: String): Bitmap? {
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