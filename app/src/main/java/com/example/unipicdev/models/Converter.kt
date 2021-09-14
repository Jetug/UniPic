package com.example.unipicdev.models

import java.io.File

fun Array<File>.toThumbnailArray(): Array<ThumbnailModel> {
    val buffList = mutableListOf<ThumbnailModel>()
    this.forEach {
        buffList.add(ThumbnailModel(it))
    }
    return buffList.toTypedArray()
}

fun ArrayList<File>.toThumbnailMutableList(): MutableList<ThumbnailModel> {
    val buffList = mutableListOf<ThumbnailModel>()
    this.forEach {
        buffList.add(ThumbnailModel(it))
    }
    return buffList
}


fun Array<File>.containsMediaFiles(): Boolean {
    this.forEach {
        if (it.isMediaFile()) return true
    }
    return false
}