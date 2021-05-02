package com.example.unipic.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.example.unipic.R
import com.example.unipic.models.interfaces.ItemOnClickListener
import java.io.File

abstract class ThumbnailAdapterBaseRV<HolderType: ThumbnailAdapterBase.ThumbnailHolder>
    (private var files: MutableList<File>, private val size: Int, private var onClickListener: ItemOnClickListener):
        RecyclerView.Adapter<HolderType>()
{
    open class ThumbnailHolder(view: View) : DragDropSwipeAdapter.ViewHolder(view) {
        val nameTV: TextView = view.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = view.findViewById<View>(R.id.mainLayout) as ConstraintLayout
        val dragIcon = view.findViewById<View>(R.id.dragIcon) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderType {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image, parent, false)
        return ThumbnailHolder(itemView) as HolderType
    }

    abstract fun onBindViewHolder2(viewHolder: HolderType, position: Int)

    override fun onBindViewHolder(viewHolder: HolderType, position: Int) {
        val item = files[position]
        viewHolder.image.setOnClickListener{
            onClickListener.onClick(item.absolutePath)
        }

        setLayoutSize(viewHolder.mainLayout, size)
        viewHolder.nameTV.text = item.name

        onBindViewHolder2(viewHolder, position)
    }


    override fun getItemCount(): Int {
        return files.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}