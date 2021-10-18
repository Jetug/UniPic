package com.example.unipicdev.views.adapters

import android.annotation.SuppressLint
import android.app.DialogFragment
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unipicdev.R
import com.example.unipicdev.models.*
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.views.adapters.SortingType.*
import com.example.unipicdev.views.adapters.ThumbnailAdapterBase.*
import com.example.unipicdev.views.controls.GalleryRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener.OnAdvancedDragSelectListener
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.extensions.applyColorFilter
import com.simplemobiletools.commons.extensions.baseConfig


enum class SortingType{
    NONE, NAME, CREATION_DATE, MODIFICATION_DATE, CUSTOM
}

enum class Order{
    NONE, ASCENDING, DESCENDING
}

abstract class ThumbnailAdapterBase<HolderType : ThumbnailHolder>(
    var activity: AppCompatActivity,
    var recyclerView: GalleryRecyclerView,
    var files: MutableList<ThumbnailModel>,
    private val size: Int,
    private var onClickListener: ItemOnClickListener)
    :RecyclerView.Adapter<HolderType>()
{
    open class ThumbnailHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val nameTV: TextView = itemView.findViewById<View>(R.id.nameTV) as TextView
        val imageView: ImageView = itemView.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = itemView.findViewById<View>(R.id.mainLayout) as ConstraintLayout
        val checkCircle = itemView.findViewById<View>(R.id.checkCircle) as ImageView
        val dragHandle = itemView.findViewById<View>(R.id.dir_drag_handle) as ImageView

        override fun onClick(view: View) {
            //onItemClick(view, adapterPosition)
        }
        override fun onLongClick(view: View): Boolean {
            return true
        }

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
    }

    protected val selectedItem: ThumbnailModel
        get() = selectedItems[0];
    private val selectedItemsCount: Int
        get() = selectedItems.count()
    protected val isOneItemSelected: Boolean
        get() = selectedItemsCount == 1
    private val selectedCount
        get() = "${selectedItems.size}/${files.count()}"

    protected val selectedItems: MutableList<ThumbnailModel> = mutableListOf()
    protected val allFiles: MutableList<ThumbnailModel> = mutableListOf()
    protected val resources = activity.resources!!
    protected val layoutInflater: LayoutInflater = activity.layoutInflater
    protected var actionMode: ActionMode? = null

    private val baseConfig = activity.baseConfig
    private var textColor = baseConfig.textColor
    private var actionModeCallback: ActionMode.Callback
    private var actBarTextView: TextView? = null
    private var selectionCounter = ""
    private val dataSaver = DataSaver()
    private lateinit var mDragSelectTouchListener: DragSelectTouchListener
    private lateinit var itemTouchHelper:ItemTouchHelper

    abstract val actionMenuId: Int

    open var sortingType: SortingType = NAME
    open var sortingOrder: Order = Order.ASCENDING

    var selectionMode: Boolean = false
        set(value) {
            if(value != field) {
                if (value)
                    enterSelectionMode()
                else
                    exitSelectionMode()
                field = value
            }
        }

    var dragMode:Boolean = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            if (value)
                selectionMode = true
                notifyDataSetChanged()
            field = value
        }

    abstract fun prepareActionMode(menu: Menu)
    abstract fun actionItemPressed(id: Int)

    init {
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
                //cancelSelecting()
                actionMode = null
                selectionMode = false
            }
        }
    }

    private fun onItemClick(view: View, position: Int){
        if (selectionMode || dragMode){
            if(actionMode == null)
                startActionMode()
            toggleSelection(position)
        }
        else {
            var pos: Int = 0
            val item = files[position]
            if(view.getTag(R.id.tag_item) != null) {
                pos = view.getTag(R.id.tag_item) as Int
            }
            onClickListener.onClick(item.file.absolutePath, pos)
        }
    }

    private fun onItemLongClick(view: View, position: Int): Boolean{
        if(!dragMode) {
            selectionMode = true
            toggleSelection(position)
        }

        if (actionMode == null && !dragMode)
            startActionMode()

        recyclerView.setDragSelectActive(position)
        mDragSelectTouchListener.startDragSelection(position);
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolder: HolderType, position: Int) {
        val item = files[position]
        viewHolder.itemView.setTag(R.id.tag_item, position)
        viewHolder.itemView.tag = viewHolder
        viewHolder.imageView.setOnClickListener { onItemClick(it, position) }
        viewHolder.imageView.setOnLongClickListener{ onItemLongClick(it, position) }
        viewHolder.dragHandle.setOnTouchListener { _, _ ->
            itemTouchHelper.startDrag(viewHolder)
            false
        }
        setLayoutSize(viewHolder.mainLayout, size)
        viewHolder.nameTV.text = item.file.name
        viewHolder.checkCircle.visibility = if(files[position].isChecked) View.VISIBLE else View.INVISIBLE
        //viewHolder.dragHandle.visibility = if(dragMode) View.VISIBLE else View.INVISIBLE

        if(dragMode){
            viewHolder.dragHandle.visibility = View.VISIBLE
            viewHolder.dragHandle.applyColorFilter(textColor)
        }
        else viewHolder.dragHandle.visibility = View.INVISIBLE
    }

    override fun onViewRecycled(holder: HolderType) {
        super.onViewRecycled(holder)
        if (!activity.isDestroyed) {
            Glide.with(activity).clear(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        val itemTouchHelperCallback = ItemMoveCallback()

        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val onDragSelectionListener = object : OnAdvancedDragSelectListener {
            override fun onSelectChange(start: Int, end: Int, isSelected: Boolean) {
                selectRange(start, end, isSelected)
            }

            override fun onSelectionStarted(start: Int) {
                // drag selection was started at index start
            }

            override fun onSelectionFinished(end: Int) {
                // drag selection was finished at index start
            }
        }

        mDragSelectTouchListener = DragSelectTouchListener() // check region OnDragSelectListener for more infos
                .withSelectListener(onDragSelectionListener)
            .withDebug(true)

        recyclerView.addOnItemTouchListener(mDragSelectTouchListener);

    }

    open fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder){

    }

    @RequiresApi(Build.VERSION_CODES.O)
    open fun sort(sortingType: SortingType, order: Order = Order.ASCENDING){
        this.sortingType = sortingType
    }

    @RequiresApi(Build.VERSION_CODES.O)
    open fun addItem(file: ThumbnailModel){
        CoroutineScope(Dispatchers.Main).launch {
            files.add(file)
            val position = files.indexOf(file)
            notifyItemInserted(position)
            sort(sortingType)
        }
    }

    private fun enterSelectionMode(){
        if(actionMode == null) {
            startActionMode()
        }
    }

    private fun exitSelectionMode(){
        if(actionMode != null){
            finishActionMode()
        }
        unselectAll()
        dragMode = false
    }

    private fun select(position: Int){
        val item = files[position]
        item.isChecked = true
        selectedItems.add(item)

        updateTitle()
    }

    private fun unselect(position: Int){
        val item = files[position]
        item.isChecked = false
        selectedItems.remove(item)

        if(selectedItems.count() == 0 && !dragMode)
            actionMode?.finish()
        else updateTitle()
    }

    private fun toggleSelection(position: Int){
        val item = files[position]

        if (!item.isChecked)
            select(position)
        else unselect(position)

        notifyItemChanged(position)
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

    open fun selectRange(start: Int, end: Int, selected: Boolean) {
        for (i in start..end) {
            if (selected) select(i) else unselect(i)
        }
        notifyItemRangeChanged(start, end - start + 1)
    }

    protected fun selectAll(){
        for ((i, _) in files.withIndex()){
            files[i].isChecked = true
            notifyItemChanged(i)
        }
        selectedItems.clear()
        selectedItems.addAll(files)f

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

    protected fun createDialog(dialog: DialogFragment){
        val manager = activity.supportFragmentManager
        dialog.show(manager, "RenamingDialog")
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

    private fun isItemSelected(pos: Int):Boolean{
        return files[pos].isChecked
    }

    inner class ItemMoveCallback: ItemTouchHelper.SimpleCallback(
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
            super.clearView(recyclerView, viewHolder)
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false//dragMode
        }
    }
}