
package com.example.unipic.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlin.reflect.typeOf

abstract class ThumbnailAdapterBase< HolderType: ThumbnailAdapterBase.ThumbnailHolder>
    ( private val size: Int, private var onClickListener: ItemOnClickListener, private var files: ArrayList<File> = ArrayList()):
        DragDropSwipeAdapter<File, HolderType>(files)
{
    open class ThumbnailHolder(view: View) : ViewHolder(view) {
        val nameTV: TextView = view.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = view.findViewById<View>(R.id.mainLayout) as ConstraintLayout
        val dragIcon = view.findViewById<View>(R.id.dragIcon) as ImageView
    }

    abstract fun onBindViewHolder2(item: File, viewHolder: HolderType, position: Int)

    override fun onBindViewHolder(item: File, viewHolder: HolderType, position: Int) {
        viewHolder.image.setOnClickListener{
            onClickListener.onClick(item.absolutePath)
        }

        setLayoutSize(viewHolder.mainLayout, size)
        viewHolder.nameTV.text = item.name

        onBindViewHolder2(item, viewHolder, position)
    }


    override fun getViewHolder(itemView: View): HolderType {
        return ThumbnailHolder(itemView) as HolderType
    }

    override fun getViewToTouchToStartDraggingItem(item: File, viewHolder: HolderType, position: Int): View? {
        return viewHolder.dragIcon
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun <T> createItem(view: View, method: (View) -> T): T {
        return method(view)
    }

    inline fun <reified T : HolderType> getValue(): T {
        val primaryConstructor = T::class.constructors.find { it.parameters.isEmpty() }
        return primaryConstructor!!.call()
    }
}