package com.example.unipic.views.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.unipic.R
import com.example.unipic.models.MediaSearcher
import com.example.unipic.models.interfaces.ItemOnClickListener
import java.io.File


class ImageAdapter(private val files: ArrayList<File>, private val onClickListener: ItemOnClickListener) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    val mediaSearcher = MediaSearcher()

    inner class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thumbnail, parent, false)
        return ImageHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val file = files[position]
        holder.nameTV.text = file.name
        val bImage = mediaSearcher.getThumbnail(file.absolutePath)
        holder.image.setImageBitmap(bImage)
    }

    override fun getItemCount(): Int {
        return files.size
    }

}