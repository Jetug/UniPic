package com.example.unipic.views.adapters

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.*
import com.example.unipic.*
import com.example.unipic.models.showFullImage
import java.io.*

class ImageViewerAdapter(private val images: ArrayList<File>): RecyclerView.Adapter<ImageViewerAdapter.Holder>()
{
    class Holder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById<View>(R.id.imagePV) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_viewer, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val image = images[position]
        showFullImage(image, holder.imageView.context ,holder.imageView, )
    }

    override fun getItemCount(): Int {
        return images.size
    }
}