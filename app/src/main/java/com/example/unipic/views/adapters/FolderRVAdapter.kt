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
import java.io.*

class FolderRvAdapter(private var files: ArrayList<File>, private val size: Int, private var onClickListener: ItemOnClickListener):
        ThumbnailAdapterBase<FolderRvAdapter.FolderHolder>(files, size, onClickListener)
{
    class FolderHolder(view: View) : ThumbnailHolder(view) {

    }

    override fun getViewHolder(itemView: View):FolderHolder = FolderHolder(itemView)

    override fun onBindViewHolder2(item: File, viewHolder: FolderHolder, position: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getFolderThumbnail(item.absolutePath, size)
            withContext(Dispatchers.Main){
                viewHolder.image.setImageBitmap(bImage)
            }
        }
    }
}