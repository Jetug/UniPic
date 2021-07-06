package com.example.unipicdev.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.unipicdev.R
import com.example.unipicdev.models.*
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.models.isHidden
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderRVAdapter(activity: AppCompatActivity, files: MutableList<FolderModel>, private val size: Int, private var onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBaseRV<FolderRVAdapter.FolderHolder>(activity, mutableListOf(), size, onClickListener)
{
    class FolderHolder(view: View): ThumbnailHolder(view)

    private var hiddenFolders: MutableList<FolderModel> = mutableListOf()
    private var usualFolders: MutableList<FolderModel> = mutableListOf()

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

    init{

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderRVAdapter.FolderHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_folder, parent, false)
        return FolderHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: FolderHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        val item = files[position]

        imageCreator.showThumbnail( (item as FolderModel).images[0], viewHolder.imageView.context, viewHolder.imageView, size)

//        CoroutineScope(Dispatchers.Main).launch {
//            imageCreator.showFolderThumbnail(item.file, viewHolder.imageView.context, viewHolder.imageView, size)
//        }
    }



    fun addItem(file: FolderModel) {
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