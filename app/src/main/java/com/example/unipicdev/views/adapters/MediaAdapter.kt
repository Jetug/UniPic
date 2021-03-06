package com.example.unipicdev.views.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.unipicdev.*
import com.example.unipicdev.models.*
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.models.room.DatabaseApi
import com.example.unipicdev.views.controls.GalleryRecyclerView
import com.example.unipicdev.views.dialogs.DateEditingDialog
import com.example.unipicdev.views.dialogs.*
import java.io.File
import kotlin.system.measureTimeMillis


@RequiresApi(Build.VERSION_CODES.O)
class MediaAdapter(activity: AppCompatActivity,
                   recyclerView: GalleryRecyclerView,
                   medias: MutableList<ThumbnailModel>,
                   private var size: Int,
                   private val directory: String,
                   onClickListener: ItemOnClickListener,
                   private val listener: OnPaintingClickListener
)
    : ThumbnailAdapterBase<MediaAdapter.MediaHolder>(activity, recyclerView, medias, size, onClickListener)
{
    private val dataSaver = DataSaver()
    private val imageCreator = ImageFactory() 

    override val actionMenuId: Int
        get() = R.menu.menu_media_work

    init{
        val pair = DatabaseApi.getMediaSorting(directory)
        val sorting: SortingType = getNotNoneSortingType(pair.first)
        val order: Order = getNotNoneSortingOrder(pair.second)
        sort(sorting, order)
    }

    class MediaHolder(view: View) : ThumbnailHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)

        val holder =  MediaHolder(itemView)
        holder.imageView.setOnClickListener(this::onItemClick);
        return holder;
        //return MediaHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MediaHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)

        // Storing item position for click handler
        viewHolder.itemView.setTag(R.id.tag_item, position)

        val item = files[position]
//        var longVal = item.file.lastModified()
//        var date = Date(longVal)
//        val time = date.time
        imageCreator.showThumbnail(item.file, viewHolder.imageView.context, viewHolder.imageView, size, true)
    }

    override fun prepareActionMode(menu: Menu) {
        val isOneItemSelected = isOneItemSelected
    }

    override fun actionItemPressed(id: Int) {
        if(selectedItems.isEmpty()) return

        when(id){
            R.id.rename -> rename()
            R.id.options -> options()
            R.id.editDate -> editDate()
            R.id.moveTo -> moveTo()
            R.id.copyTo -> copyTo()
            R.id.delete -> delete()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun sort(sortingType: SortingType, order: Order) {
        super.sort(sortingType, order)
        val time = measureTimeMillis {
            val sortedList = sortMedias(files.toTypedArray(), sortingType, order)
            files = sortedList.toMutableList()
            notifyDataSetChanged()
        }
        Log.d("My", "Sorting $time ms")

        //CoroutineScope(Dispatchers.Default).launch{
        DatabaseApi.saveMediaSorting(directory, sortingType, order)
        //}

        mediaSortingType = sortingType
        mediaSortingOrder = order
    }

    override fun swapItems(fromPosition: Int, toPosition: Int){
        super.swapItems(fromPosition, toPosition)
        dataSaver.saveImagePositions(directory ,files)
    }

    private fun rename(){
        @RequiresApi(Build.VERSION_CODES.O)
        fun onRename(position: Int, file: File) {
            val item = selectedItems[position]
            item.file = file
            changeItem(item, item)
            //sort(sortingType)
        }

        fun onComplete(newFiles: MutableList<File>) {
//            for (position in 0 until newFiles.size) {
//                val item = selectedItems[position]
//                val newItem = item
//                newItem.file = newFiles[position]
//                changeItem(item, newItem)
//            }
            if(sortingType == SortingType.NAME)
                sort(sortingType)
            selectionMode = false
        }

        val dialog = MediaRenamingDialog(selectedItems.map{it.file}, ::onRename, ::onComplete)
        createDialog(dialog)
    }

    private fun options(){
        if(isOneItemSelected){

        }
        else{

        }
    }

    private fun editDate(){
        val dialog = DateEditingDialog(selectedItems) { _, _ -> selectionMode = false }
        if(sortingType == SortingType.MODIFICATION_DATE || sortingType == SortingType.CREATION_DATE)
            sort(sortingType)
        createDialog(dialog)
    }

    private fun moveTo(){
    }

    private fun copyTo(){
    }

    private fun delete(){
        val dialog = DeletingDialog{
            selectedItems.forEach{
                deleteFile(it.file)
                removeItem(it)
            }
            selectionMode = false
        }
        createDialog(dialog)
    }

    companion object {
        fun getImageView(holder: RecyclerView.ViewHolder): ImageView {
            return (holder as MediaHolder).imageView
        }
    }

    private fun onItemClick(view: View) {
        val pos = view.getTag(R.id.tag_item) as Int
        listener.onPaintingClick(pos)
    }
}

interface OnPaintingClickListener {
    fun onPaintingClick(position: Int)
}