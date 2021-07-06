package com.example.unipicdev.views.adapters

import android.view.View
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageAdapter (private var files: ArrayList<File>, private val size:Int, private var onClickListener: ItemOnClickListener):
        ThumbnailAdapterBase<ImageAdapter.ImageHolder>(size, onClickListener, files) {

    class ImageHolder(view: View) : ThumbnailHolder(view) {

    }

    override fun getViewHolder(itemView: View):ImageHolder = ImageHolder(itemView)

    override fun onBindViewHolder2(item: File, viewHolder: ImageHolder, position: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getThumbnail(item.absolutePath, size)
            withContext(Dispatchers.Main){
                viewHolder.image.setImageBitmap(bImage)
            }
        }
    }

    override fun onDragFinished(item: File, viewHolder: ImageHolder) {
        val test = dataSet
    }
}