package com.example.unipic.views.adapters

import android.os.Build
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.unipic.R
import com.example.unipic.models.DataSaver
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.views.adapters.SortingType.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Files.readAttributes
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

enum class SortingType{
    NAME, CREATION_DATE, MODIFICATION_DATE, CUSTOM
}

enum class Order{
    UP, DOWN
}

abstract class ThumbnailAdapterBaseRV<HolderType : ThumbnailAdapterBaseRV.ThumbnailHolder>(
        var files: MutableList<ThumbnailModel>,
        private val size: Int,
        private var onClickListener: ItemOnClickListener)
    :RecyclerView.Adapter<HolderType>()
{
    open class ThumbnailHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById<View>(R.id.nameTV) as TextView
        val imageView: ImageView = itemView.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = itemView.findViewById<View>(R.id.mainLayout) as ConstraintLayout
        val checkCircle = itemView.findViewById<View>(R.id.checkCircle) as ImageView
        //val dragIcon = itemView.findViewById<View>(R.id.dragIcon) as ImageView
    }

    private val dataSaver = DataSaver()
    private lateinit var recyclerView: RecyclerView
    private var sorting: SortingType = NAME
    var selectionMode = false
    private val selectedItems: MutableList<ThumbnailModel> = mutableListOf()
    protected val allFiles: MutableList<ThumbnailModel> = mutableListOf()

    init {
        sort(sorting)
    }

    var isDragEnabled = false

    fun cancelSelecting(){
        for ((i, file) in files.withIndex()){
            files[i].isChecked = false
            selectedItems.clear()
            notifyItemChanged(i)
        }
        selectionMode = false
    }

    override fun onBindViewHolder(viewHolder: HolderType, position: Int) {
        val item = files[position]

        fun select(){
            if ( viewHolder.checkCircle.visibility == View.INVISIBLE){
                viewHolder.checkCircle.visibility = View.VISIBLE
                files[position].isChecked = true
                selectedItems.add(item)
            }
            else{
                viewHolder.checkCircle.visibility = View.INVISIBLE
                files[position].isChecked = false
                selectedItems.remove(item)
            }

        }

        viewHolder.checkCircle.visibility = View.INVISIBLE

        viewHolder.imageView.setOnLongClickListener {
            if(!isDragEnabled) {
                selectionMode = true
                select()
            }
            return@setOnLongClickListener true
        }

        viewHolder.imageView.setOnClickListener{
            if (selectionMode || isDragEnabled){
                select()
            }
            else onClickListener.onClick(item.file.absolutePath)
        }

        setLayoutSize(viewHolder.mainLayout, size)
        viewHolder.nameTV.text = item.file.name
        if(files[position].isChecked)
            viewHolder.checkCircle.visibility = View.VISIBLE
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

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                this@ThumbnailAdapterBaseRV.clearView(recyclerView, viewHolder)
                super.clearView(recyclerView, viewHolder)
            }

            override fun isLongPressDragEnabled(): Boolean {
                return isDragEnabled
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    open fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder){

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    open fun sort(sortingType: SortingType, reverse: Boolean = true){
        fun reverse(){if(reverse) files.reverse()}

        when(sortingType){
            NAME -> {
                files.sortBy { it.file.name }
                reverse()
            }
            CREATION_DATE -> {
                files.sortBy {
                    val path = FileSystems.getDefault().getPath(it.file.absolutePath)
                    val attr = readAttributes<BasicFileAttributes>(path, BasicFileAttributes::class.java)
                    return@sortBy attr.creationTime()
                }
                reverse()
            }
            MODIFICATION_DATE -> {
                files.sortBy { Date(it.file.lastModified()) }
                reverse()
            }
            CUSTOM -> {
            }
        }
        notifyDataSetChanged()
    }

    fun reorderItems(custom: MutableList<ThumbnailModel>){
        CoroutineScope(Dispatchers.Default).launch {
            for (i in 0 until custom.size) {
                for (j in 0 until files.size) {
                    if (files[j].file.name == custom[i].file.name) {
                        files.add(i, files[j])
                        files.removeAt(j + 1)
                        notifyItemMoved(j, i)
                        break
                    }
                }
            }
        }
    }

    open fun addItem(file: ThumbnailModel){
        CoroutineScope(Dispatchers.Main).launch {
            files.add(file)
            val position = files.indexOf(file)
            notifyItemInserted(position)
            sort(sorting)
        }
    }

    open fun removeItem(file: ThumbnailModel){
        //CoroutineScope(Dispatchers.Main).launch {
            var position = 0
            for (i in 0 until files.size){
                if (files[i] == file) {
                    position = i
                    break
                }
            }

            files.removeAt(position)
            notifyItemRemoved(position)

            //sort(sorting)
        //}
    }

    open fun swapItems(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            files.add(toPosition + 1, files[fromPosition])
            files.removeAt(fromPosition)
        } else {
            files.add(toPosition, files[fromPosition])
            files.removeAt(fromPosition + 1)
        }

        notifyItemMoved(fromPosition, toPosition)
    }
}