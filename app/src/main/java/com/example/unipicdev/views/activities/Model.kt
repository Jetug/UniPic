package com.example.unipicdev.views.activities

import android.app.Activity
import android.util.DisplayMetrics
import com.example.unipicdev.models.ImageCreator
import com.example.unipicdev.models.MediaSearcher

val mediaSearcher  = MediaSearcher()
val imageCreator = ImageCreator()

fun getDisplaySize(activity: Activity): DisplayMetrics {
    val display = activity.windowManager.defaultDisplay
    val metricsB = DisplayMetrics()
    display.getMetrics(metricsB)
    return metricsB
}