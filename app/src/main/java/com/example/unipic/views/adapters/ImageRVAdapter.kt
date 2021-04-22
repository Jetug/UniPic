package com.example.unipic.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.unipic.R
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ImageRVAdapter(private val files: ArrayList<File>, var size: Int, private val onClickListener: ItemOnClickListener) : RecyclerView.Adapter<ImageRVAdapter.ImageHolder>() {

    inner class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = view.findViewById<View>(R.id.mainLayout) as ConstraintLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val file = files[position]

        holder.image.setOnClickListener{
            onClickListener.onClick(file.absolutePath)
        }

        setLayoutSize(holder.mainLayout, size)
        holder.nameTV.text = file.name
        holder.image.setImageBitmap(null)

        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getThumbnail(file.absolutePath, size)
            withContext(Dispatchers.Main){
                holder.image.setImageBitmap(bImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }
}