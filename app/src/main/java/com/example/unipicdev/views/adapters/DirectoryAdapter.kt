package com.example.unipicdev.views.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.unipicdev.*
import com.example.unipicdev.models.*
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.models.isHidden
import com.example.unipicdev.models.room.DatabaseApi
import com.example.unipicdev.views.controls.GalleryRecyclerView
import com.example.unipicdev.views.dialogs.DeletingDialog
import com.example.unipicdev.views.dialogs.PropertiesDialog
import kotlinx.coroutines.*

@RequiresApi(Build.VERSION_CODES.O)
class DirectoryAdapter(activity: AppCompatActivity, recyclerView: GalleryRecyclerView, dirs: MutableList<ThumbnailModel>, private val size: Int, onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBase<DirectoryAdapter.FolderHolder>(activity, recyclerView, dirs, size, onClickListener)
{
    private val imageCreator = ImageFactory()

    class FolderHolder(view: View): ThumbnailHolder(view)

    private var hiddenFolders: MutableList<FolderModel> = mutableListOf()
    private var usualFolders: MutableList<FolderModel> = mutableListOf()

    override val actionMenuId: Int
        get() = R.menu.menu_directory_work

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
        //setupDragListener(true)

        sort(directorySortingType, directorySortingOrder)
        DatabaseApi.addOnMediaSortingSaved {
            for (i in 0 until files.size) {
                if(files[i].file.absolutePath == it){
                    notifyItemChanged(i)
                    break
                }
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            //checkForSortingChanged()
        }
    }

    private suspend fun checkForSortingChanged(){
        while (true){
            delay(5000)
            withContext(Dispatchers.Main) {
                notifyItemChanged(0)
            }
            Log.d("My", "updated")
//            files.forEach{
//
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryAdapter.FolderHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_folder, parent, false)
        return FolderHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: FolderHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        //viewHolder.setIsRecyclable(false)

        val item = files[position]

        CoroutineScope(Dispatchers.Default).launch {
            //imageCreator.showFolderThumbnail( (item as FolderModel).images.toThumbnailArray(), viewHolder.imageView.context, viewHolder.imageView, size)
            imageCreator.showFolderThumbnail(item.file, viewHolder.imageView.context, viewHolder.imageView, size)
        }
    }

    override fun prepareActionMode(menu: Menu) {
        val isOneItemSelected = isOneItemSelected
    }

    override fun actionItemPressed(id: Int) {
        if(selectedItems.isEmpty()) return

        when(id){
            R.id.options -> options()
            R.id.delete -> delete()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun sort(sortingType: SortingType, order: Order) {
        super.sort(sortingType, order)

        val sortedList = sortDirs(files.toTypedArray(), sortingType, order)
        files = sortedList.toMutableList()
        notifyDataSetChanged()

        directorySortingType = sortingType
        directorySortingOrder = order
    }

    private fun rename(){
        if(isOneItemSelected){

        }
        else{

        }
    }

    private fun options(){
        if(isOneItemSelected){
            val dialog = PropertiesDialog(selectedItem.file)
            createDialog(dialog)
        }
        else{

        }
    }

    private fun editDate(){
        if(isOneItemSelected){

        }
        else{

        }
    }

    private fun moveTo(){

    }

    private fun copyTo(){

    }

    private fun delete(){
        val dialog = DeletingDialog{
            selectedItems.forEach{
                it.file.deleteALLMedias()
                removeItem(it)
            }
            selectionMode = false
        }
        createDialog(dialog)
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