package com.example.unipic.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.unipic.R
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        viewHolder.setIsRecyclable(false);
        val item = files[position]
        //if (item.bitmap == null) {
            CoroutineScope(Dispatchers.Default).launch {
                val bImage = imageCreator.getFolderThumbnail(item.file.absolutePath, size)
                //item.bitmap = bImage
                withContext(Dispatchers.Main) {
                    viewHolder.imageView.setImageBitmap(bImage)
                }
            }
        //}
        //else viewHo lder.image.setImageBitmap(item.bitmap)
    }


}