package com.example.unipicdev.models

import com.example.unipicdev.views.adapters.Order
import com.example.unipicdev.views.adapters.SortingType
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

fun sortDirs(list:Array<File>, sortingType: SortingType, sortingOrder: Order): Array<File>{
    var media = list

    fun reverse(){if(sortingOrder == Order.DESCENDING) media.reverse()}

    when (sortingType) {
        SortingType.NONE -> {

        }
        SortingType.NAME -> {
            media.sortBy { it.name }
            reverse()
        }
        SortingType.CREATION_DATE -> {
            media.sortBy {
                val path = FileSystems.getDefault().getPath(it.absolutePath)
                val attr = Files.readAttributes(path, BasicFileAttributes::class.java)
                return@sortBy attr.creationTime()
            }
            reverse()
        }
        SortingType.MODIFICATION_DATE -> {
            media.sortBy { it.lastModified() }
            reverse()
        }
    }

    return media
}

private fun sortMedias(list:Array<File>, sortingType: SortingType, sortingOrder: Order): Array<File>{
    var media = list

    if (sortingType == SortingType.CUSTOM){
        val dataSaver = DataSaver()
        media = dataSaver.getCustomMediaListF(media[0].parent).toTypedArray()
    }
    else sortDirs(list, sortingType, sortingOrder)

    return media
}

