package com.example.unipic.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.unipic.R
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class FolderRVAdapter(private var files: MutableList<File>, private val size: Int, private var onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBaseRV<FolderRVAdapter.FolderHolder>(files, size, onClickListener)
{
    class FolderHolder(view: View): ThumbnailHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderRVAdapter.FolderHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_folder, parent, false)
        return FolderHolder(itemView)
    }

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val file = files[position]
        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getFolderThumbnail(file.absolutePath, size)
            withContext(Dispatchers.Main){
                holder.image.setImageBitmap(bImage)
            }
        }
    }


}