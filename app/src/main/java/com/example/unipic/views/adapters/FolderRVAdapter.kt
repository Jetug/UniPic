package com.example.unipic.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.unipic.R
import com.example.unipic.models.interfaces.ItemOnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class FolderRVAdapter(private var files: MutableList<File>, private val size: Int, private var onClickListener: ItemOnClickListener)
    : RecyclerView.Adapter<FolderRVAdapter.FolderHolder>()
{
    class FolderHolder(view: View): RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById<View>(R.id.nameTV) as TextView
        val image: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
        val mainLayout = view.findViewById<View>(R.id.mainLayout) as ConstraintLayout
    }

    fun onBindViewHolder2(item: File, viewHolder: FolderHolder, position: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            val bImage = imageCreator.getFolderThumbnail(item.absolutePath, size)
            withContext(Dispatchers.Main){
                viewHolder.image.setImageBitmap(bImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderRVAdapter.FolderHolder {
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

    fun addItem(file: File){
        files.add(file)
        val position = files.indexOf(file)

        notifyItemInserted(position)
    }
}