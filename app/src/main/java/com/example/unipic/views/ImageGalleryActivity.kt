package com.example.unipic.views

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unipic.R
import com.example.unipic.models.MediaSearcher
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.views.adapters.FolderAdapter
import com.example.unipic.views.adapters.ImageAdapter
import kotlinx.android.synthetic.main.activity_image_gallery.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_folder.*
import java.io.File
import java.net.URL
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*

class ImageGalleryActivity : AppCompatActivity(){

    private val mediaSearcher = MediaSearcher()
    private val colCount = 3;
    private val imageGalleryActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_gallery)

        val linearLayoutManager = GridLayoutManager(applicationContext, 3)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        imagesRV.layoutManager = linearLayoutManager
        imagesRV.setHasFixedSize(true)

        CoroutineScope(Dispatchers.Default).launch{
            initImageRV()
        }
    }

    private fun initImageRV(){
        val intent = intent

        if (intent != null){
            val dirPath = intent.getCharSequenceExtra("dirPath")
            val dir = File(dirPath as String)
            val imagesList = mediaSearcher.getImageFiles(dirPath as String)

            val size: DisplayMetrics = getDisplaySize(this)
            val width = size.widthPixels / colCount

            imagesRV.adapter = ImageAdapter(imagesList, width, object: ItemOnClickListener {
                override fun onClick(path: String) {
                    val imageActivityIntent = Intent(imageGalleryActivity, ImageActivity::class.java)
                    imageActivityIntent.putExtra("imagePath", path)
                    startActivity(imageActivityIntent)
                }
            })
        }
    }
}