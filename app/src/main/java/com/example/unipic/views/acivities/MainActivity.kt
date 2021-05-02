package com.example.unipic.views.acivities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.example.unipic.R
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.views.adapters.*
import ir.androidexception.filepicker.dialog.DirectoryPickerDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File
import java.lang.reflect.GenericArrayType


class MainActivity : AppCompatActivity() {

    private val colCount = 2;
    private val mainActivity = this;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener(::clickFun)
        testBtn.setOnClickListener(::clickTest)


        //CoroutineScope(Dispatchers.Main).launch {
            initFolderRV();
        //}

        //requestPermission()

        val pref = getSharedPreferences("Table", Context.MODE_PRIVATE)
        val editor = pref.edit()
    }

    private fun clickTest(v: View){

    }

    private fun initFolderRV(){
        val linearLayoutManager = GridLayoutManager(applicationContext, colCount)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        dndRV.layoutManager = linearLayoutManager
        dndRV.setHasFixedSize(true)
        //dndRV.orientation = DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_UNCONSTRAINED_DRAGGING

        //val folderList = arrayListOf<File>(File("/storage/emulated/0/UniPic/"))

        val size: DisplayMetrics = getDisplaySize(mainActivity)
        val width = size.widthPixels / colCount

        dndRV.adapter = FolderRVAdapter(ArrayList(), width, object : ItemOnClickListener {
            override fun onClick(path: String) = folderItemOnClick(path)
        })


         fun addFolderItem(file:File){
            CoroutineScope(Dispatchers.Main).launch{
                (dndRV.adapter as FolderRVAdapter).addItem(file)
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
        if (permissionGranted()) {
            val directoryPickerDialog = DirectoryPickerDialog(
                this,
                {
                    Toast.makeText(
                        this@MainActivity,
                        "Canceled!!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                {
                    files: Array<File> ->
                    Toast.makeText(
                        this@MainActivity,
                        files[0].path,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
            directoryPickerDialog.show()
        } else {
            requestPermission()
        }
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

//             runBlocking {
//                 withContext(Dispatchers.Main) {
//                     (dndRV.adapter as FolderAdapter).addItem(file)
//                     (dndRV.adapter as FolderAdapter).notifyDataSetChanged()
//                 }
//             }