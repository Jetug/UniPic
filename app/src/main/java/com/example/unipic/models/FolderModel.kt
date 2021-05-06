package com.example.unipic.models

import android.graphics.Bitmap
import java.io.File

class FolderModel (file: File):ThumbnailModel(file) {
    private val imageCreator = ImageCreator()
    private var _bitmap: Bitmap? = null
//    val bitmap: Bitmap?
//        get() {
//            if (_bitmap == null){
//                _bitmap = imageCreator.getFolderThumbnail(file.absolutePath, size)
//            }
//            return _bitmap
//        }
}