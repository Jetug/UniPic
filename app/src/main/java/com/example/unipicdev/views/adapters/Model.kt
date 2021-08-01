package com.example.unipicdev.views.adapters

import android.view.ViewGroup
import com.example.unipicdev.models.ImageFactory
import com.example.unipicdev.models.MediaSearcher

val mediaSearcher  = MediaSearcher()
val imageCreator = ImageFactory()

fun setLayoutSize(view: ViewGroup, size: Int){
    val layoutParams = view.layoutParams
    layoutParams.width = size
    layoutParams.height = size
    view.layoutParams = layoutParams
}