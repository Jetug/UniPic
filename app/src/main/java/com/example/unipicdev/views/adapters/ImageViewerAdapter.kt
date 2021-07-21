package com.example.unipicdev.views.adapters

import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.example.unipicdev.*
import com.example.unipicdev.models.showFullImage
import java.io.*

class ImageViewerAdapter(val activity: AppCompatActivity, private val images: ArrayList<File>): RecyclerView.Adapter<ImageViewerAdapter.Holder>()
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
        showFullImage(image, holder.imageView.context ,holder.imageView, true)

        activity.actionBar?.title = images[position].name
    }

    override fun getItemCount(): Int {
        return images.size
    }
}