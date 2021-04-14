package com.example.unipic.views.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.unipic.R
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.unipic.models.ImageCreator
import com.example.unipic.models.MediaSearcher
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.*
import java.io.File

class FolderAdapter(var files: ArrayList<File>, val size:Int, var onClickListener: ItemOnClickListener)
    : RecyclerView.Adapter<FolderAdapter.FolderHolder>()
{
    inner class FolderHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = view.findViewById<View>(R.id.mainLayout) as ConstraintLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderAdapter.FolderHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderHolder(itemView)
    }

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        val file = files[position]

        holder.image.setOnClickListener{
            onClickListener.onClick(file.absolutePath)
        }

        setLayoutSize(holder.mainLayout, size)
        holder.nameTV.text = file.name
        holder.image.setImageBitmap(null)

        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getFolderThumbnail(file.absolutePath, size)
            withContext(Dispatchers.Main){
                holder.image.setImageBitmap(bImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }
}