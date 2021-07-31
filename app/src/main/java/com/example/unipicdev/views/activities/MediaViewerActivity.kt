package com.example.unipicdev.views.activities

import android.os.Bundle
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.unipicdev.R
import com.example.unipicdev.views.adapters.ImageViewerAdapter
import com.veinhorn.scrollgalleryview.ScrollGalleryView
import github.hellocsl.layoutmanager.gallery.GalleryLayoutManager
//import kotlinx.android.synthetic.main.activity_image.*
import java.io.File


class MediaViewerActivity : AppCompatActivity()/*, ViewSwitcher.ViewFactory*/{

    private var position = 0
    private var images = arrayListOf<File>()
    private val currentMedia: File
        get() = images[position]

    private lateinit var mImageSwitcher: ImageSwitcher
    private lateinit var mGestureDetector: GestureDetector
    private lateinit var galleryView: ScrollGalleryView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        initRecyclerView()
    }

    private fun initRecyclerView(){
        val images = intent.getSerializableExtra("imageList") as ArrayList<File>
        val position = intent.getIntExtra("position",0)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL)
        layoutManager.attach(recyclerView, position)

        val adapter = ImageViewerAdapter(this, images)
        recyclerView.adapter = adapter

        supportActionBar?.title = ""
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_media, menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home -> {
                onHome()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onHome(){
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        if (backStackEntryCount > 0) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        }
        onBackPressed()
    }
}