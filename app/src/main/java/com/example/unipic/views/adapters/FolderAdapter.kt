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

class FolderAdapter(private var files: ArrayList<File>, private val size: Int, private var onClickListener: ItemOnClickListener): DragDropSwipeAdapter<File, FolderAdapter.FolderHolder>(files) {

    class FolderHolder(view: View) : DragDropSwipeAdapter.ViewHolder(view) {
        val nameTV: TextView = view.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = view.findViewById<View>(R.id.mainLayout) as ConstraintLayout
        val dragIcon = view.findViewById<View>(R.id.dragIcon) as ImageView
    }

    override fun getViewHolder(itemView: View): FolderHolder = FolderHolder(itemView)

    override fun onBindViewHolder(item: File, viewHolder: FolderHolder, position: Int) {
        viewHolder.image.setOnClickListener{
            onClickListener.onClick(item.absolutePath)
        }

        setLayoutSize(viewHolder.mainLayout, size)
        viewHolder.nameTV.text = item.name

        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getFolderThumbnail(item.absolutePath, size)
            withContext(Dispatchers.Main){
                viewHolder.image.setImageBitmap(bImage)
            }
        }
    }

    override fun getViewToTouchToStartDraggingItem(item: File, viewHolder: FolderHolder, position: Int): View {
        // We return the view holder's view on which the user has to touch to drag the item
        return viewHolder.dragIcon
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}