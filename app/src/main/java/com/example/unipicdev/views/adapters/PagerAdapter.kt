package com.example.unipicdev.views.adapters

import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter
import com.alexvasilkov.gestures.views.GestureImageView
import com.example.unipicdev.models.showFullImage
import java.io.File

internal class PagerAdapter( private val viewPager: ViewPager, val medias: List<File>) :
    RecyclePagerAdapter<PagerAdapter.ViewHolder>()
{
    internal class ViewHolder(container: ViewGroup) : RecyclePagerAdapter.ViewHolder(GestureImageView(container.context)) {
        val image: GestureImageView = itemView as GestureImageView
    }

    override fun getCount(): Int {
        return medias.size
    }

    override fun onCreateViewHolder(container: ViewGroup): ViewHolder {
        val holder = ViewHolder(container)
        holder.image.controller.enableScrollInViewPager(viewPager)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val media = medias[position]
        showFullImage(media, holder.image.context ,holder.image, true)
    }

    companion object {
        fun getImageView(holder: RecyclePagerAdapter.ViewHolder): GestureImageView {
            return (holder as ViewHolder).image
        }
    }
}