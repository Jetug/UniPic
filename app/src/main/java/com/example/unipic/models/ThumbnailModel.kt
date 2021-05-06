package com.example.unipic.models

import android.graphics.Bitmap
import java.io.File

open class ThumbnailModel (var file: File) {
    var bitmap: Bitmap? = null
}