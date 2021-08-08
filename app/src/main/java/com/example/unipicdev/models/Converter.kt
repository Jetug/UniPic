package com.example.unipicdev.models

import java.io.File

fun Array<File>.toThumbnailArray(): Array<ThumbnailModel> {
    val buffList = mutableListOf<ThumbnailModel>()
    this.forEach {
        buffList.add(ThumbnailModel(it))
    }
    return buffList.toTypedArray()
}