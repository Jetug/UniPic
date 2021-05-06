package com.example.unipic.models

import android.graphics.Bitmap
import java.io.File

class ImageModel(var file:File, var size: Int) {
    private val imageCreator = ImageCreator()
    private var _bitmap: Bitmap? = null
    val bitmap: Bitmap?
        get() {
            if (_bitmap == null){
                _bitmap = imageCreator.getThumbnail(file.absolutePath, size)
            }
            return _bitmap
        }
}