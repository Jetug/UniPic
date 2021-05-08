package com.example.unipic.views.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.unipic.R
import com.example.unipic.models.DataSaver
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImageRVAdapter(private var files: MutableList<ThumbnailModel>, private val context: Context, private var size: Int, private val directory: String, onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBaseRV<ImageRVAdapter.ImageHolder>(files, size, onClickListener)
{
    private val dataSaver = DataSaver()

    class ImageHolder(view: View) : ThumbnailHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return ImageHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ImageHolder, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        viewHolder.setIsRecyclable(false);
        val item = files[position]
        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getThumbnail(item.file.absolutePath, size)
            withContext(Dispatchers.Main) {
                viewHolder.image.setImageBitmap(bImage)
            }
        }
        Glide.with(viewHolder.image.context).load(item.file).into(viewHolder.image)
    }

    override fun sort(sortingType: SortingType, reverse: Boolean) {
        if (sortingType == SortingType.CUSTOM) {
            CoroutineScope(Dispatchers.Default).launch {
                val custom = dataSaver.getImagePositions(directory)

                for (i in 0 until custom.size){
                    for (j in 0 until files.size){
                        if(files[j].file.name == custom[i].file.name){
                            files.add(i, files[j])
                            files.removeAt(j+1)
                            //withContext(Dispatchers.Main){
                            notifyItemMoved(j,i)
                            //}
                            break;
                        }
                    }
                }
            }
        }
        else super.sort(sortingType, reverse)
    }

//    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//        super.clearView(recyclerView, viewHolder)
//        dataSaver.saveImagePositions(directory ,files)
//    }

    override fun swapItems(fromPosition: Int, toPosition: Int){
        super.swapItems(fromPosition, toPosition)
        dataSaver.saveImagePositions(directory ,files)
    }

}