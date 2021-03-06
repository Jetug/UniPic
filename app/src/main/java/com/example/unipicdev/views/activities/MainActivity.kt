package com.example.unipicdev.views.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unipicdev.R
import com.example.unipicdev.appContext
import com.example.unipicdev.models.*
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.views.adapters.DirectoryAdapter
import com.example.unipicdev.views.controls.GalleryRecyclerView
import com.example.unipicdev.views.dialogs.SortingDialog
//import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : BaseActivity() {
    private val colCount = 2
    private val mainActivity = this
    private lateinit var folderAdapter: DirectoryAdapter
    private lateinit var directorySearcher: DirectorySearcher

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appContext = applicationContext;
        isStoragePermissionGranted()
        directorySearcher = DirectorySearcher(appContext)

        CoroutineScope(Dispatchers.Default).launch {
            initFolderRV()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.sorting -> sort()
            R.id.dragMode ->enableDragging()
            R.id.showHidden -> showHidden(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enableDragging(){
        folderAdapter.dragMode = true
    }

    private fun showHidden(item: MenuItem){
        if(!folderAdapter.showHidden) {
            folderAdapter.showHidden = true
            item.title = resources.getString(R.string.dont_show_hidden)
        }
        else{
            folderAdapter.showHidden = false
            item.title = resources.getString(R.string.show_hidden)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sort(){
        val dialog = SortingDialog(true){
            sorting, order ->
            folderAdapter.sort(sorting, order)
        }
        dialog.show(supportFragmentManager, "SortingDialog")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun initFolderRV(){
        val linearLayoutManager = GridLayoutManager(applicationContext, colCount)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val recyclerView = findViewById<GalleryRecyclerView>(R.id.dndRV)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)

        val size: DisplayMetrics = getDisplaySize(mainActivity)
        val width = size.widthPixels / colCount

        //val savedDirs = directorySearcher.getSavedDirectories().toThumbnailMutableList()
        val savedDirs = mutableListOf<ThumbnailModel>()

        val adapter = DirectoryAdapter(this, recyclerView, savedDirs, width, object : ItemOnClickListener {
            override fun onClick(path: String, position: Int) = folderItemOnClick(path, position)
        })
        withContext(Dispatchers.Main){
            recyclerView.adapter = adapter
        }
        folderAdapter = adapter

         @RequiresApi(Build.VERSION_CODES.O)
         fun addFolderItem(file: FolderModel){
            CoroutineScope(Dispatchers.Main).launch{
                (adapter).addItem(file)
            }
        }

         directorySearcher.getDirectories(::addFolderItem)
    }

    private fun folderItemOnClick(path: String, pos:Int){
        val intent = Intent(mainActivity, MediaGalleryActivity::class.java)
        intent.putExtra("dirPath", path)
        startActivity(intent)
    }
}