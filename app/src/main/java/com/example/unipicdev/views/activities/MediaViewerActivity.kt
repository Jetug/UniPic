package com.example.unipicdev.views.activities

import android.os.Bundle
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.unipicdev.R
import com.example.unipicdev.views.adapters.ImageViewerAdapter
import com.example.unipicdev.views.adapters.ViewPagerAdapter
import com.veinhorn.scrollgalleryview.ScrollGalleryView
import github.hellocsl.layoutmanager.gallery.GalleryLayoutManager
//import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

class MediaViewerActivity : BaseActivity(){

    private var position = 0
    private var images = arrayListOf<File>()
    private val currentMedia: File
        get() = images[position]


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        initRecyclerView()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_media, menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return true
    }

    private fun initRecyclerView(){
        val images = intent.getSerializableExtra("imageList") as ArrayList<File>
        val position = intent.getIntExtra("position",0)

        updateTitle(images[position])

        var viewPager: ViewPager? = null
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = ViewPagerAdapter(this, viewPager, images)
        viewPager.currentItem = position;
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float,positionOffsetPixels: Int) {
                //updateTitle(images[position])
            }

            override fun onPageSelected(position: Int) {
                updateTitle(images[position])
            }

        })
    }

    private fun updateTitle(currentMedia: File){
        supportActionBar?.title = currentMedia.nameWithoutExtension
    }
}