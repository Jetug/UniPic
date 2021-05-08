package com.example.unipic.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.example.unipic.R
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.views.adapters.*
//import ir.androidexception.filepicker.dialog.DirectoryPickerDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val colCount = 2;
    private val mainActivity = this;
    private lateinit var folderAdapter: FolderRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener(::clickFun)

            //Glide.with(this).load(File("/storage/emulated/0/Pictures/@Test/T.jpg")).into(image);

        CoroutineScope(Dispatchers.Main).launch {
            initFolderRV();
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.byName -> folderAdapter.sort(SortingType.NAME)
            R.id.byCreationDate -> folderAdapter.sort(SortingType.CREATION_DATE)
            R.id.byModificationDate -> folderAdapter.sort(SortingType.MODIFICATION_DATE)
            R.id.custom -> folderAdapter.sort(SortingType.CUSTOM)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initFolderRV(){
        val linearLayoutManager = GridLayoutManager(applicationContext, colCount)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        dndRV.layoutManager = linearLayoutManager
        dndRV.setHasFixedSize(true)

        //val folderList = arrayListOf<File>(File("/storage/emulated/0/UniPic/"))

        val size: DisplayMetrics = getDisplaySize(mainActivity)
        val width = size.widthPixels / colCount

        val adapter = FolderRVAdapter(ArrayList(), width, object : ItemOnClickListener {
            override fun onClick(path: String) = folderItemOnClick(path)
        })
        dndRV.adapter = adapter
        folderAdapter = adapter

         @RequiresApi(Build.VERSION_CODES.O)
         fun addFolderItem(file:File){
            CoroutineScope(Dispatchers.Main).launch{
                (adapter).addItem(ThumbnailModel(file))
            }
         }

        mediaSearcher.getDirectories(::addFolderItem)
    }

    private fun folderItemOnClick(path: String){
        val intent = Intent(mainActivity, ImageGalleryActivity::class.java)
        intent.putExtra("dirPath", path)
        startActivity(intent)
    }

    private fun clickFun(v: View) {
//        if (permissionGranted()) {
//            val directoryPickerDialog = DirectoryPickerDialog(
//                this,
//                {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Canceled!!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                },
//                {
//                    files: Array<File> ->
//                    Toast.makeText(
//                        this@MainActivity,
//                        files[0].path,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            )
//            directoryPickerDialog.show()
//        } else {
//            requestPermission()
//        }
    }

    private fun permissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            1
        )
    }
}