package com.example.unipic.views.adapters

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.example.unipic.R
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.views.adapters.SortingType.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

enum class SortingType{
    NAME, CREATION_DATE, MODIFICATION_DATE, CUSTOM
}

enum class Order{
    UP, DOWN
}

abstract class ThumbnailAdapterBaseRV<HolderType : ThumbnailAdapterBaseRV.ThumbnailHolder>(
        private var files: MutableList<ThumbnailModel>,
        private val size: Int,
        private var onClickListener: ItemOnClickListener)
    :RecyclerView.Adapter<HolderType>()
{

    open class ThumbnailHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = itemView.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = itemView.findViewById<View>(R.id.mainLayout) as ConstraintLayout
        //val dragIcon = itemView.findViewById<View>(R.id.dragIcon) as ImageView

        private var buffImage: Bitmap? = null
    }

    private lateinit var recyclerView: RecyclerView
    private var sorting: SortingType = NAME
    override fun onBindViewHolder(viewHolder: HolderType, position: Int) {
        val item = files[position]
        viewHolder.image.setOnClickListener{
            onClickListener.onClick(item.file.absolutePath)
        }

        setLayoutSize(viewHolder.mainLayout, size)
        viewHolder.nameTV.text = item.file.name
    }


    override fun getItemCount(): Int {
        return files.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                0)
            {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
                {
                    swapItems(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){}
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun sort(sortingType: SortingType, reverce: Boolean = true){
        when(sortingType){
            NAME -> {
                files.sortBy { it.file.name }
                if (reverce) files. reverse()
            }
            CREATION_DATE -> files.sortBy { it.file.name }
            MODIFICATION_DATE ->{
                files.sortBy{ Date(it.file.lastModified()) }
                if (reverce) files. reverse()
            }
            //CUSTOM -> TODO()
        }
        notifyDataSetChanged()
    }

    fun addItem(file: ThumbnailModel){
        CoroutineScope(Dispatchers.Main).launch {
            files.add(file)
            val position = files.indexOf(file)

            notifyItemInserted(position)

            sort(sorting)
        }
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition..toPosition - 1) {
                files[i] = files.set(i + 1, files[i]);
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                files[i] = files.set(i - 1, files[i]);
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }


}