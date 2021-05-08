package com.example.unipic.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.unipic.R
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.models.supportedExtentions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FolderRVAdapter(private var files: MutableList<ThumbnailModel>, private val size: Int, private var onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBaseRV<FolderRVAdapter.FolderHolder>(files, size, onClickListener)
{
    class FolderHolder(view: View): ThumbnailHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderRVAdapter.FolderHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_folder, parent, false)
        return FolderHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: FolderHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        val item = files[position]
        imageCreator.showFolderThumbnail(item.file, viewHolder.imageView.context, viewHolder.imageView, size)
    }

}