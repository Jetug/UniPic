package com.example.unipicdev.models

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import kotlin.Comparator

@RequiresApi(Build.VERSION_CODES.O)
fun sortDirs(list:Array<ThumbnailModel>, sortingType: SortingType, sortingOrder: Order): Array<ThumbnailModel>{
    val dirs = list

    fun reverse(): Array<ThumbnailModel> {
        if(sortingOrder == Order.DESCENDING) dirs.reverse()
        return dirs
    }

    dirs.sortWith(Comparator { o1, o2 ->
        o1 as ThumbnailModel
        o2 as ThumbnailModel

        val result = when (sortingType) {
            SortingType.NAME -> {
                AlphanumericComparator().compare(o1.file.name.lowercase(), o2.file.name.lowercase()) //Locale.getDefault()
            }
            SortingType.CREATION_DATE -> {
                val path1 = FileSystems.getDefault().getPath(o1.file.absolutePath)
                val path2 = FileSystems.getDefault().getPath(o2.file.absolutePath)
                val attr1 = Files.readAttributes(path1, BasicFileAttributes::class.java)
                val attr2 = Files.readAttributes(path2, BasicFileAttributes::class.java)
                (attr1.creationTime()).compareTo(attr2.creationTime())
            }
            SortingType.MODIFICATION_DATE -> {
                (o1.file.lastModified()).compareTo(o2.file.lastModified())
            }
            else -> (o1.file.name.toLongOrNull() ?: 0).compareTo(o2.file.name.toLongOrNull() ?: 0)
        }
        return@Comparator result
    })
    reverse()
    return dirs
}

@RequiresApi(Build.VERSION_CODES.O)
fun sortMedias(list:Array<ThumbnailModel>, sortingType: SortingType, sortingOrder: Order): Array<ThumbnailModel> {
    var media = list

    if (sortingType == SortingType.CUSTOM){
        val dataSaver = DataSaver()
        val dir = media[0].file.parent
        if(dir != null)
            media = dataSaver.getCustomMediaList(dir).toTypedArray()
    }
    else sortDirs(list, sortingType, sortingOrder)

    return media
}

