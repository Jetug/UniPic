package com.example.unipic.views.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.example.unipic.R
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageAdapter (var files: ArrayList<File>, val size:Int, var onClickListener: ItemOnClickListener)
    : DragDropSwipeAdapter<File, ImageAdapter.ImageHolder>(files) {

    class ImageHolder(view: View) : DragDropSwipeAdapter.ViewHolder(view) {
        val nameTV: TextView = view.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = view.findViewById<View>(R.id.mainLayout) as ConstraintLayout
        val dragIcon = view.findViewById<View>(R.id.dragIcon) as ImageView
    }

    override fun getViewHolder(itemLayout: View): ImageAdapter.ImageHolder = ImageAdapter.ImageHolder(itemLayout)

    override fun onBindViewHolder(item: File, holder: ImageAdapter.ImageHolder, position: Int) {
        val file = files[position]

        holder.image.setOnClickListener{
            onClickListener.onClick(file.absolutePath)
        }

        setLayoutSize(holder.mainLayout, size)
        holder.nameTV.text = file.name
        holder.image.setImageBitmap(null)

        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getThumbnail(file.absolutePath, size)
            withContext(Dispatchers.Main){
                holder.image.setImageBitmap(bImage)
            }
        }
    }

    override fun getViewToTouchToStartDraggingItem(item: File, viewHolder: ImageHolder, position: Int): View {
        // We return the view holder's view on which the user has to touch to drag the item
        return viewHolder.dragIcon
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}