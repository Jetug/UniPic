package com.example.unipicdev.views.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.unipicdev.R
import com.example.unipicdev.models.*
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.views.adapters.SortingType.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.FileSystems
import java.nio.file.Files.readAttributes
import java.nio.file.Files.setAttribute
import java.nio.file.attribute.BasicFileAttributes
import java.util.*


enum class SortingType{
    NONE, NAME, CREATION_DATE, MODIFICATION_DATE, CUSTOM
}

enum class Order{
    NONE, ASCENDING, DESCENDING
}

abstract class ThumbnailAdapterBase<HolderType : ThumbnailAdapterBase.ThumbnailHolder>(
        var activity: AppCompatActivity,
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
    }

    protected val selectedItem: ThumbnailModel
        get() = selectedItems[0];
    protected val selectedItemsCount: Int
        get() = selectedItems.count()
    protected val isOneItemSelected: Boolean
        get() = selectedItemsCount == 1
    protected val selectedCount
        get() = "${selectedItems.size}/${files.count()}"

    protected val selectedItems: MutableList<ThumbnailModel> = mutableListOf()
    protected val allFiles: MutableList<ThumbnailModel> = mutableListOf()
    protected val resources = activity.resources!!
    protected var positionOffset = 0
    protected val layoutInflater: LayoutInflater = activity.layoutInflater
    protected var actionMode: ActionMode? = null
    protected var actionModeCallback: ActionMode.Callback

    private var actBarTextView: TextView? = null
    private var selectionCounter = ""
    private val dataSaver = DataSaver()
    private lateinit var recyclerView: RecyclerView

    abstract val actionMenuId: Int

    var sortingType: SortingType = NAME
    var sortingOrder: Order = Order.ASCENDING


    var selectionMode = false
    get() = field
    set(value) {
        if (value)
            startActionMode()
//        else
//            finishActionMode()
        field = value
    }

    var isDragEnabled = false

    abstract fun prepareActionMode(menu: Menu)
    abstract fun actionItemPressed(id: Int)

    init {
        //sort(sortingType)

        actionModeCallback = object: ActionMode.Callback {
            override fun onActionItemClicked(mode: android.view.ActionMode?, item: MenuItem?): Boolean {
                if(item != null)
                    actionItemPressed(item.itemId)
                return false;
            }

            @SuppressLint("InflateParams")
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {

                actionMode = mode

                if (mode != null) {
                    actBarTextView = layoutInflater.inflate(R.layout.actionbar_title, null) as TextView
                    actBarTextView!!.layoutParams = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    actionMode!!.customView = actBarTextView
                    actBarTextView!!.setOnClickListener {
                        if(selectedItems.size != files.size)
                            selectAll()
                        else
                            unselectAll()
                    }
                    activity.menuInflater.inflate(actionMenuId, menu)
                    updateTitle()
                }
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                if(menu != null)
                    prepareActionMode(menu)
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                cancelSelecting()
                actionMode = null;
            }
        }
    }



    override fun onBindViewHolder(viewHolder: HolderType, position: Int) {
        val item = files[position]

        fun select(){
            if ( viewHolder.checkCircle.visibility == View.INVISIBLE){
                viewHolder.checkCircle.visibility = View.VISIBLE
                files[position].isChecked = true
                selectedItems.add(item)

                updateTitle()
            }
            else{
                viewHolder.checkCircle.visibility = View.INVISIBLE
                files[position].isChecked = false
                selectedItems.remove(item)

                if(selectedItems.count() == 0){
                    actionMode?.finish()
                }
                else{
                    updateTitle()
                }
            }
        }

        viewHolder.checkCircle.visibility = View.INVISIBLE

        viewHolder.imageView.setOnClickListener{
            if (selectionMode || isDragEnabled){
                if(actionMode == null)
                    startActionMode()
                select()
            }
            else onClickListener.onClick(item.file.absolutePath)
        }

        viewHolder.imageView.setOnLongClickListener {
            if(!isDragEnabled) {
                selectionMode = true
                select()
            }

            if (actionMode == null && !isDragEnabled)
                startActionMode()

            return@setOnLongClickListener true
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
                this@ThumbnailAdapterBase.clearView(recyclerView, viewHolder)
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

    @RequiresApi(Build.VERSION_CODES.O)
    open fun sort(sortingType: SortingType, order: Order = Order.ASCENDING){
        fun reverse()
        {
            if(order == Order.DESCENDING) files.reverse()
        }

        when(sortingType){
            NAME -> {
                files.sortBy { it.file.name }
                reverse()
            }
            CREATION_DATE -> {
                files.sortBy {
                    val path = FileSystems.getDefault().getPath(it.file.absolutePath)
                    val attr = readAttributes(path, BasicFileAttributes::class.java)
                    return@sortBy attr.creationTime()
                }
                reverse()
            }
            MODIFICATION_DATE -> {
                files.sortBy{ it.file.lastModified() }
                reverse()
            }
            else -> {}
        }
        notifyDataSetChanged()
        this.sortingType = sortingType
    }

    open fun addItem(file: ThumbnailModel){
        CoroutineScope(Dispatchers.Main).launch {
            files.add(file)
            val position = files.indexOf(file)
            notifyItemInserted(position)
            sort(sortingType)
        }
    }

    private fun getPositionOfItem(item: Any, list: List<Any>): Int{
        for (i in list.indices){
            if (list[i] == item) {
                return i
            }
        }
        return -1
    }

    open fun removeItem(file: ThumbnailModel){
        val position = getPositionOfItem(file, files)
        files.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun changeItem(oldItem: ThumbnailModel, newItem: ThumbnailModel){
        val position = getPositionOfItem(oldItem, files)
        files[position] = newItem
        notifyItemChanged(position)
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

    protected fun selectAll(){
        for ((i, _) in files.withIndex()){
            files[i].isChecked = true
            notifyItemChanged(i)
        }
        selectedItems.clear()
        selectedItems.addAll(files)

        //selectionMode = true
        updateTitle()
    }

    protected fun unselectAll(){
        for ((i, _) in files.withIndex()){
            files[i].isChecked = false
            notifyItemChanged(i)
        }
        selectedItems.clear()
        updateTitle()
    }

    protected fun cancelSelecting(){
        unselectAll()
        selectionMode = false
    }

    protected fun delete(){
        selectedItems.forEach{
            deleteFile(it.file)
            removeItem(it)
        }
    }

    private fun startActionMode(){
        actionMode = activity.startActionMode(actionModeCallback)
    }

    private fun finishActionMode() {
        actionMode?.finish()
    }

    private fun updateTitle(){
        actBarTextView?.text = selectedCount
    }
}