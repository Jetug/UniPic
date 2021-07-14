package com.example.unipicdev.views.adapters

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.unipicdev.R
import com.example.unipicdev.models.DataSaver
import com.example.unipicdev.models.ThumbnailActionModeCallback
import com.example.unipicdev.models.ThumbnailModel
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.views.adapters.SortingType.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        //val dragIcon = itemView.findViewById<View>(R.id.dragIcon) as ImageView
    }

    private val dataSaver = DataSaver()
    private lateinit var recyclerView: RecyclerView
    private var sorting: SortingType = NAME

    protected val selectedItems: MutableList<ThumbnailModel> = mutableListOf()
    protected val allFiles: MutableList<ThumbnailModel> = mutableListOf()

    protected val resources = activity.resources!!
    protected var positionOffset = 0
    protected val layoutInflater = activity.layoutInflater
    protected var actionMode: android.view.ActionMode? = null
    protected var actModeCallback: ThumbnailActionModeCallback

    private var actBarTextView: TextView? = null
    private var selectionCounter = ""

    abstract val actionMenuId: Int

    abstract fun prepareActionMode(menu: Menu)
    abstract fun actionItemPressed(id: Int)

    var selectionMode = false
    var isDragEnabled = false

    init {
        sort(sorting)

        actModeCallback = object:ThumbnailActionModeCallback(){
            override fun onActionItemClicked(mode: android.view.ActionMode?, item: MenuItem?): Boolean {
                if(item != null)
                    actionItemPressed(item.itemId)
                return false;
            }

            @SuppressLint("InflateParams")
            override fun onCreateActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                isSelectable = true
                actionMode = mode

                if (mode != null) {
//                    val customView: View = activity.layoutInflater.inflate(R.layout.actionbar_title, null)
//                    val customTitle = customView.findViewById<View>(R.id.actionbarTitle) as TextView
//                    customTitle.setOnClickListener {
//                        if(selectedItems.size != files.size)
//                            selectAll()
//                        else
//                            cancelSelecting()
//                    }
//                    mode.customView = customView

//                    activity.supportActionBar?.apply {
//                        customView = actionBarCustomTitle()
//                        setDisplayHomeAsUpEnabled(true)
//
//                        customView.setOnClickListener {
//                            if(selectedItems.size != files.size)
//                                selectAll()
//                            else
//                                cancelSelecting()
//                        }
//
//                        displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//
//                        setDisplayShowHomeEnabled(true)
//                    }

                    val actMode = actionMode
                    actBarTextView = layoutInflater.inflate(R.layout.actionbar_title, null) as TextView
                    actBarTextView!!.layoutParams = ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    actMode!!.customView = actBarTextView
                    actBarTextView!!.setOnClickListener {
                        if(selectedItems.size != files.size)
                            selectAll()
                        else
                            unselectAll()
                    }

                    //actBarTextView!!.text = getTitle()

                    activity.menuInflater.inflate(actionMenuId, menu)

                    updateTitle()

                    //actMode.title = getTitle()
                    //mode.title = getTitle()
                }
                return true
            }

            override fun onPrepareActionMode(mode: android.view.ActionMode?, menu: Menu?): Boolean {
                if(menu != null)
                    prepareActionMode(menu)
                return true
            }

            override fun onDestroyActionMode(mode: android.view.ActionMode?) {
                cancelSelecting()
                actionMode = null;
            }
        }
    }

    fun selectAll(){
        for ((i, _) in files.withIndex()){
            files[i].isChecked = true
            notifyItemChanged(i)
        }
        selectedItems.clear()
        selectedItems.addAll(files)

        selectionMode = true
        updateTitle()
    }

    fun unselectAll(){
        for ((i, _) in files.withIndex()){
            files[i].isChecked = false
            notifyItemChanged(i)
        }
        selectedItems.clear()
        updateTitle()
    }

    fun cancelSelecting(){
        unselectAll()
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

                if(selectedItems.count() == 0){
                    actionMode?.finish()
                }
            }
//            selectionCounter = getTitle()
//            actionMode?.title = selectionCounter

            updateTitle()
        }



        viewHolder.checkCircle.visibility = View.INVISIBLE

        viewHolder.imageView.setOnLongClickListener {
            if(!isDragEnabled) {
                selectionMode = true
                select()
            }


            if (actionMode == null && !isDragEnabled)
                actionMode = activity.startActionMode(actModeCallback)

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

    private fun getTitle(): String = "${selectedItems.size}/${files.count()}"

    private fun updateTitle(){
        actBarTextView?.text = getTitle()
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
    open fun sort(sortingType: SortingType, reverse: Boolean = false){
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

    fun finishActMode() {
        actionMode?.finish()
    }

    fun getSelectedItemsCount(): Int = selectedItems.count()

    fun isOneItemSelected():Boolean = getSelectedItemsCount() == 1

    private fun actionBarCustomTitle():TextView{
        return TextView(activity).apply {
            text = resources.getString(R.string.app_name);

            val params = ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.LEFT
            layoutParams = params

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(
                    android.R.style.TextAppearance_Material_Widget_ActionBar_Title
                )
            }else{
                // define your own text style
                setTextSize(TypedValue.COMPLEX_UNIT_SP,17F)
                setTypeface(null, Typeface.BOLD)
            }
        }
    }
}