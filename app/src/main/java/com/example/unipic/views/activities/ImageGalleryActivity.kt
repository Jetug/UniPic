package com.example.unipic.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unipic.R
import com.example.unipic.models.MediaSearcher
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.views.adapters.ImageRVAdapter
import com.example.unipic.views.adapters.SortingType
import kotlinx.android.synthetic.main.activity_image_gallery.*
import java.io.File

class ImageGalleryActivity : AppCompatActivity(){

    private val mediaSearcher = MediaSearcher()
    private val colCount = 3;
    private val imageGalleryActivity = this
    private lateinit var imageAdapter: ImageRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_gallery)

//        val linearLayoutManager = GridLayoutManager(applicationContext, 3)
//        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
//        imagesRV.layoutManager = linearLayoutManager
//        imagesRV.setHasFixedSize(true)
//
//        CoroutineScope(Dispatchers.Default).launch{
//            initImageRV()
//        }

        initImageRV();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_gallery, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.byName -> imageAdapter.sort(SortingType.NAME)
            R.id.byCreationDate -> imageAdapter.sort(SortingType.CREATION_DATE)
            R.id.byModificationDate -> imageAdapter.sort(SortingType.MODIFICATION_DATE)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initImageRV(){
        val linearLayoutManager = GridLayoutManager(applicationContext, colCount)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        imagesDRV.layoutManager = linearLayoutManager
        imagesDRV.setHasFixedSize(true)
        //imagesDRV.orientation = DragDropSwipeRecyclerView.ListOrientation.HORIZONTAL_LIST_WITH_UNCONSTRAINED_DRAGGING

        val dirPath = intent.getCharSequenceExtra("dirPath")as String

        val imagesList = ArrayList<ThumbnailModel>()// mediaSearcher.getImageFiles(dirPath )

        val size: DisplayMetrics = getDisplaySize(this)
        val width = size.widthPixels / colCount
        val adapter = ImageRVAdapter(imagesList, width, object : ItemOnClickListener {
            override fun onClick(path: String) {
                val imageActivityIntent = Intent(imageGalleryActivity, ImageActivity::class.java)
                imageActivityIntent.putExtra("imagePath", path)
                startActivity(imageActivityIntent)
            }
        })

        imagesDRV.adapter = adapter
        imageAdapter = adapter

        fun addFolderItem(file: File){
            (adapter).addItem(ThumbnailModel(file))
        }

        mediaSearcher.getImageFiles(dirPath, ::addFolderItem)
    }
}