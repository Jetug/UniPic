package com.example.unipic.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.unipic.R
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.models.isHidden
import com.example.unipic.models.supportedExtentions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FolderRVAdapter(files: MutableList<ThumbnailModel>, private val size: Int, private var onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBaseRV<FolderRVAdapter.FolderHolder>(files, size, onClickListener)
{
    class FolderHolder(view: View): ThumbnailHolder(view)

    private var hiddenFolders: MutableList<ThumbnailModel> = mutableListOf()
    private var usualFolders: MutableList<ThumbnailModel> = mutableListOf()

    var showHidden: Boolean = false
        set(value) {
            CoroutineScope(Dispatchers.Default).launch {
                if (value) {
                    for (item in hiddenFolders) {
                        super.addItem(item)
                    }
                } else {
                    for (item in hiddenFolders) {
                        removeItem(item)
                    }
                }

                field = value
            }
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

    override fun addItem(file: ThumbnailModel) {
        if(isHidden(file.file)){
            hiddenFolders.add(file)
        }
        else{
            usualFolders.add(file)
            super.addItem(file)
        }
    }

//    override fun addItem(file: ThumbnailModel) {
//        if(!isHidden(file.file))
//            super.addItem(file)
//    }
}