package com.example.unipic.views.adapters

import android.view.ViewGroup
import com.example.unipic.models.ImageCreator
import com.example.unipic.models.MediaSearcher

val mediaSearcher  = MediaSearcher()
val imageCreator = ImageCreator()

fun setLayoutSize(view: ViewGroup, size: Int){
    val layoutParams = view.layoutParams
    layoutParams.width = size
    layoutParams.height = size
    view.layoutParams = layoutParams
}