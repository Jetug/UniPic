package com.example.unipic.views.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import com.example.unipic.R
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

abstract class ThumbnailAdapterBaseRV<HolderType : ThumbnailAdapterBaseRV.ThumbnailHolder>
    (
    private var files: MutableList<File>,
    private val size: Int,
    private var onClickListener: ItemOnClickListener
):RecyclerView.Adapter<HolderType>()
{
    init {
        //this.setHasStableIds(true)
    }

    open class ThumbnailHolder(itemView: View) : DragDropSwipeAdapter.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = itemView.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = itemView.findViewById<View>(R.id.mainLayout) as ConstraintLayout
        //val dragIcon = itemView.findViewById<View>(R.id.dragIcon) as ImageView
    }

    private lateinit var recyclerView: RecyclerView

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderType {
//        val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_image, parent, false)
//        return ThumbnailHolder(itemView) as HolderType
//    }

    //abstract fun onBindViewHolder2(viewHolder: HolderType, position: Int)

    override fun onBindViewHolder(viewHolder: HolderType, position: Int) {
        val item = files[position]
        viewHolder.image.setOnClickListener{
            onClickListener.onClick(item.absolutePath)
        }

        setLayoutSize(viewHolder.mainLayout, size)
        viewHolder.nameTV.text = item.name

        //onBindViewHolder2(viewHolder, position)
    }


    override fun getItemCount(): Int {
        return files.size
    }



    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    swapItems(viewHolder.adapterPosition, target.adapterPosition)
                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int){}
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun addItem(file: File){
        CoroutineScope(Dispatchers.Main).launch {
            files.add(file)
            val position = files.indexOf(file)

            notifyItemInserted(position)
        }
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition..toPosition - 1) {
                files.set(i, files.set(i + 1, files.get(i)));
            }
        } else {
            for (i in fromPosition..toPosition + 1) {
                files.set(i, files.set(i - 1, files.get(i)));
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

//    override fun getItemViewType(position: Int): Int {
//        return position
//    }
}