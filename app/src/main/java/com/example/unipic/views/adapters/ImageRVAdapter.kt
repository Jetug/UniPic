package com.example.unipic.views.adapters

//import android.widget.ImageView
//import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.unipic.R
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ImageRVAdapter(private val files: MutableList<ThumbnailModel>, var size: Int, private val onClickListener: ItemOnClickListener)
    : ThumbnailAdapterBaseRV<ImageRVAdapter.ImageHolder>(files, size, onClickListener)
{
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
        //if (item.bitmap == null) {
            CoroutineScope(Dispatchers.Default).launch {
                val bImage = imageCreator.getThumbnail(item.file.absolutePath, size)
                //item.bitmap = bImage
                withContext(Dispatchers.Main) {
                    viewHolder.image.setImageBitmap(bImage)
                }
            }
        //}
        //else viewHolder.image.setImageBitmap(item.bitmap)

    }
}