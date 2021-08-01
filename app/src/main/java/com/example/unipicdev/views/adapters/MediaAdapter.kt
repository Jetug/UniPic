package com.example.unipicdev.views.adapters

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.unipicdev.*
import com.example.unipicdev.models.DataSaver
import com.example.unipicdev.models.ThumbnailModel
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.models.room.getMediaSorting
import com.example.unipicdev.views.dialogs.DateEditingDialog
import com.example.unipicdev.views.dialogs.*
import java.io.File


class MediaAdapter(activity: AppCompatActivity, files: MutableList<ThumbnailModel>, private var size: Int, private val directory: String, onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBase<MediaAdapter.ImageHolder>(activity, files, size, onClickListener)
{
    private val dataSaver = DataSaver()

    override val actionMenuId: Int
        get() = R.menu.menu_media_work

    init{
        val pair = getMediaSorting(directory)
        val sorting: SortingType = getNotNoneSortingType(pair.first)
        val order: Order = getNotNoneSortingOrder(pair.second)
        sort(sorting, order)
    }

    class ImageHolder(view: View) : ThumbnailHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return ImageHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ImageHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
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

    override fun sort(sortingType: SortingType, order: Order) {
        if (sortingType == SortingType.CUSTOM){
            val custom = dataSaver.getCustomMediaList(directory)
            reorderItems(custom)
            super.sortingType = SortingType.CUSTOM
        }
        else super.sort(sortingType, order)

        mediaSortingType = sortingType
        mediaSortingOrder = order
    }

    override fun swapItems(fromPosition: Int, toPosition: Int){
        super.swapItems(fromPosition, toPosition)
        dataSaver.saveImagePositions(directory ,files)
    }

    private fun rename(){
        fun onRename(position: Int, file: File){
            val item = selectedItems[position]
            val newItem = item
            newItem.file = file
            changeItem(item, newItem)
            sort(sortingType)
        }

        fun onComplete(newFiles: MutableList<File>) {
            for (position in 0 until newFiles.size) {
                val item = selectedItems[position]
                val newItem = item
                newItem.file = newFiles[position]
                changeItem(item, newItem)
            }
            if(sortingType == SortingType.NAME)
                sort(sortingType)
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
        val dialog = DateEditingDialog(selectedItems)
        createDialog(dialog)
    }

    private fun moveTo(){

    }

    private fun copyTo(){

    }

    private fun createDialog(dialog: DialogFragment){
        val manager = activity.supportFragmentManager
        dialog.show(manager, "RenamingDialog")
    }

}