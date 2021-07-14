package com.example.unipicdev.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unipicdev.R
import com.example.unipicdev.models.MediaSearcher
import com.example.unipicdev.models.ThumbnailModel
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.views.adapters.MediaAdapter
import com.example.unipicdev.views.adapters.SortingType
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.android.synthetic.main.activity_image_gallery.*
import java.io.File

class MediaGalleryActivity : AppCompatActivity(){

    private val mediaSearcher = MediaSearcher()
    private val colCount = 3
    private val imageGalleryActivity = this
    private lateinit var imageAdapter: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_gallery)
        initImageRV()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_gallery, menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(menu != null){
            val sw = menu.findItem(R.id.positionCB).actionView?.findViewById<SwitchMaterial>(R.id.positionCB)
            sw?.setOnCheckedChangeListener(::onChecked)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.byName -> imageAdapter.sort(SortingType.NAME)
            R.id.byCreationDate -> imageAdapter.sort(SortingType.CREATION_DATE)
            R.id.byModificationDate -> imageAdapter.sort(SortingType.MODIFICATION_DATE)
            R.id.custom -> imageAdapter.sort(SortingType.CUSTOM)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(imageAdapter.selectionMode) {
                imageAdapter.cancelSelecting()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun onChecked(buttonView: CompoundButton, isChecked: Boolean){
        imageAdapter.isDragEnabled = isChecked
    }

    private fun initImageRV(){
        val linearLayoutManager = GridLayoutManager(applicationContext, colCount)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        imagesDRV.layoutManager = linearLayoutManager
        imagesDRV.setHasFixedSize(true)
        //imagesDRV.orientation = DragDropSwipeRecyclerView.ListOrientation.HORIZONTAL_LIST_WITH_UNCONSTRAINED_DRAGGING

        val dirPath = intent.getCharSequenceExtra("dirPath")as String
        val imageFiles = mediaSearcher.getImageFiles(dirPath)
        val imagesList = mutableListOf<ThumbnailModel>()
        val buffList = mutableListOf<File>()

        for (file in imageFiles)
        {
            imagesList.add(ThumbnailModel(file))
        }



        val arrayList = ArrayList(imagesList)

        val size: DisplayMetrics = getDisplaySize(this)
        val width = size.widthPixels / colCount
        val adapter = MediaAdapter(this, imagesList, width, dirPath, object : ItemOnClickListener {
            override fun onClick(path: String) {
                val imageActivityIntent = Intent(imageGalleryActivity, ImageActivity::class.java)

                for (file in imageAdapter.files)
                {
                    buffList.add(file.file)
                }

                var pos = 0
                for ((i, file) in buffList.withIndex()){
                    if (file.absolutePath == path){
                        pos = i
                        break
                    }
                }

                imageActivityIntent.putExtra("imagePath", path)
                imageActivityIntent.putExtra("position", pos)
                imageActivityIntent.putExtra("imageList", ArrayList<File>(buffList))

                startActivity(imageActivityIntent)
            }
        })

        imagesDRV.adapter = adapter
        imageAdapter = adapter

//        fun addFolderItem(file: File){
//            (adapter).addItem(ThumbnailModel(file))
//        }

        //mediaSearcher.showImageFiles(dirPath, ::addFolderItem)
    }
}