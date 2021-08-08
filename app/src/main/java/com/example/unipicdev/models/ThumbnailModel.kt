package com.example.unipicdev.models

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

open class ThumbnailModel (var file: File, var isChecked: Boolean = false)