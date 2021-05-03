package com.example.unipic.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.unipic.R
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ImageRVAdapter(private val files: MutableList<File>, var size: Int, private val onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBaseRV<ImageRVAdapter.ImageHolder>(files, size, onClickListener)
{
    class ImageHolder(view: View) : ThumbnailHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return ImageHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val file = files[position]
        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getThumbnail(file.absolutePath, size)
            withContext(Dispatchers.Main){
                holder.image.setImageBitmap(bImage)
            }
        }
    }
}