package com.example.unipicdev.models

import kotlinx.coroutines.*
import java.io.*

class FolderModel (file: File):ThumbnailModel(file) {

//    var images: Array<File> = arrayOf()
    init {
//        CoroutineScope(Dispatchers.Default).launch{
//            images = file.listFiles()
//        }
    }
}