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
import com.example.unipicdev.models.room.DatabaseApi.getMediaSorting
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.min
import kotlin.system.measureTimeMillis
import com.example.unipicdev.supportedExtension

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
        Log.d("My", "media input")
        if(files.isNotEmpty()) {
            val time = measureTimeMillis {
                val dirPath: String = files[0].file.parent
                val pair: Pair<SortingType, Order>
                val time = measureTimeMillis {
                    pair = getMediaSorting(dirPath)
                }
                Log.d("My", "pair $time ms")

                val sorting: SortingType = getNotNoneSortingType(pair.first)
                val order: Order = getNotNoneSortingOrder(pair.second)
                var thumbnailFile: File = File("")

                val time2 = measureTimeMillis {
                    val savedTN = thumbnails[dirPath]

                    if(thumbnails.contains(dirPath) && savedTN!!.sortingType == sorting && savedTN.sortingOrder == order && File(savedTN.path).exists()){
                        thumbnailFile = File(savedTN.path)
                    }
                    else {
                        var media = files
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
    fun showFolderThumbnail(dir: File, context: Context, imageView: ImageView, size: Int) {
        Log.d("My", "dir input")
        val time = measureTimeMillis {
            val dirPath: String = dir.absolutePath
            val pair: Pair<SortingType, Order>
            val time = measureTimeMillis {
                pair = getMediaSorting(dirPath)
            }
            Log.d("My", "pair $time ms")

            val sorting: SortingType = getNotNoneSortingType(pair.first)
            val order: Order = getNotNoneSortingOrder(pair.second)
            var thumbnailFile: File = File("")

            val time2 = measureTimeMillis {
                val savedTN = thumbnails[dirPath]
                if(thumbnails.contains(dirPath) && savedTN!!.sortingType == sorting && savedTN.sortingOrder == order && File(savedTN.path).exists()){
                    thumbnailFile = File(savedTN.path)
                }
                else {
                    val dirFiles = dir.listFiles()
                    if(dirFiles != null) {
                        var media = dirFiles.toThumbnailArray()
                        if (media.isNotEmpty()) {
                            media = sortMedias(media, sorting, order) //sort(media, sorting, order)

                            for (currentItem in media) {
                                val currentFile = currentItem.file
                                if (currentFile.isFile && isMediaFile(currentFile)) {
                                    thumbnailFile = currentFile
                                    thumbnails[dirPath] =
                                        ThumbnailSave(thumbnailFile.absolutePath, sorting, order)
                                    break
                                }
                            }
                        }
                    }
                }
            }
            Log.d("My", "when $time2 ms")

            showThumbnail(thumbnailFile, context, imageView, size, false)
        }
        Log.d("My", "showFolderThumbnail $time ms | thumbnails: ${thumbnails.size}")
    }

    fun getFolderThumbnail(path: String, size: Int): Bitmap? {
        val folder = File(path)

        var bitmap: Bitmap? = null

        val files = folder.listFiles()
        for (currentFile in files) {
            if (currentFile.isFile && supportedExtension.contains(currentFile.extension)) {
                val bmOptions = BitmapFactory.Options()
                bitmap = getThumbnail(currentFile.absolutePath, size)
                break
            }
        }
        return bitmap
    }

    private fun isMediaFile(file: File): Boolean = supportedExtension.contains(file.extension)

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