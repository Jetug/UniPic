package com.example.unipicdev.views.adapters

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.unipicdev.R
import com.example.unipicdev.models.DataSaver
import com.example.unipicdev.models.ThumbnailModel
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.views.activities.MediaRenamingDialog


class MediaAdapter(activity: AppCompatActivity, files: MutableList<ThumbnailModel>, private var size: Int, private val directory: String, onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBase<MediaAdapter.ImageHolder>(activity, files, size, onClickListener)
{
    private val dataSaver = DataSaver()

    override val actionMenuId: Int
        get() = R.menu.menu_media_work

    class ImageHolder(view: View) : ThumbnailHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return ImageHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ImageHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        val item = files[position]
        imageCreator.showThumbnail(item.file, viewHolder.imageView.context, viewHolder.imageView, size, true)
    }

    override fun prepareActionMode(menu: Menu) {
        val isOneItemSelected = isOneItemSelected()

    }

    override fun actionItemPressed(id: Int) {
        if(selectedItems.isEmpty()) return

        when(id){
            R.id.rename -> rename()
            R.id.options -> options()
            R.id.editDate -> editDate()
            R.id.moveTo -> moveTo()
            R.id.copyTo -> copyTo()
        }
    }

    override fun sort(sortingType: SortingType, reverse: Boolean) {
        if (sortingType == SortingType.CUSTOM){
            val custom = dataSaver.getImagePositions(directory)
            reorderItems(custom)
        }
        else super.sort(sortingType, reverse)
    }

    override fun swapItems(fromPosition: Int, toPosition: Int){
        super.swapItems(fromPosition, toPosition)
        dataSaver.saveImagePositions(directory ,files)
    }

    private fun rename(){
        val dialog = MediaRenamingDialog(selectedItems.map{it.file})
        val manager = activity.supportFragmentManager
        dialog.show(manager, "RenamingDialog")

        if(isOneItemSelected()){

        }
        else{

        }
    }

    private fun options(){
        if(isOneItemSelected()){

        }
        else{

        }
    }

    private fun editDate(){
        if(isOneItemSelected()){

        }
        else{

        }
    }

    private fun moveTo(){

    }

    private fun copyTo(){

    }
}