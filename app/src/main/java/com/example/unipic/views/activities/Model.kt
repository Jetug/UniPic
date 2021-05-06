package com.example.unipic.views.activities

import android.app.Activity
import android.util.DisplayMetrics
import com.example.unipic.models.ImageCreator
import com.example.unipic.models.MediaSearcher

val mediaSearcher  = MediaSearcher()
val imageCreator = ImageCreator()

fun getDisplaySize(activity: Activity): DisplayMetrics {
    val display = activity.windowManager.defaultDisplay
    val metricsB = DisplayMetrics()
    display.getMetrics(metricsB)
    return metricsB
}