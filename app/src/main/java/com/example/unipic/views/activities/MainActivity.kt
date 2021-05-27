package com.example.unipic.views.activities

//import ir.androidexception.filepicker.dialog.DirectoryPickerDialog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unipic.R
import com.example.unipic.models.DirectorySearcher
import com.example.unipic.models.ThumbnailModel
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.views.adapters.FolderRVAdapter
import com.example.unipic.views.adapters.SortingType
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    private val colCount = 2
    private val mainActivity = this
    private lateinit var folderAdapter: FolderRVAdapter
    private val directorySearcher = DirectorySearcher()

    companion object {
        const val REQUEST_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        }


         var strSDCardPath = System.getenv("SECONDARY_STORAGE")
        if (null == strSDCardPath || strSDCardPath.isEmpty()) {
            strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE")
        }

        CoroutineScope(Dispatchers.Main).launch {
            initFolderRV()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if(menu != null){
            val sw = menu.findItem(R.id.positionCB).actionView?.findViewById<SwitchMaterial>(R.id.positionCB)
            sw?.setOnCheckedChangeListener(::onChecked)
        }
        return true
    }

    private fun onChecked(buttonView: CompoundButton, isChecked: Boolean){
        folderAdapter.isDragEnabled = isChecked
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.byName -> folderAdapter.sort(SortingType.NAME)
            R.id.byCreationDate -> folderAdapter.sort(SortingType.CREATION_DATE)
            R.id.byModificationDate -> folderAdapter.sort(SortingType.MODIFICATION_DATE)
            R.id.custom -> folderAdapter.sort(SortingType.CUSTOM)
            R.id.showHidden -> {
                if(!folderAdapter.showHidden) {
                    folderAdapter.showHidden = true
                    item.title = resources.getString(R.string.dont_show_hidden)
                }
                else{
                    folderAdapter.showHidden = false
                    item.title = resources.getString(R.string.show_hidden)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(folderAdapter.selectionMode) {
                folderAdapter.cancelSelecting()
                return false
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun initFolderRV(){
        val linearLayoutManager = GridLayoutManager(applicationContext, colCount)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        dndRV.layoutManager = linearLayoutManager
        dndRV.setHasFixedSize(true)

        val size: DisplayMetrics = getDisplaySize(mainActivity)
        val width = size.widthPixels / colCount

        val adapter = FolderRVAdapter(ArrayList(), width, object : ItemOnClickListener {
            override fun onClick(path: String) = folderItemOnClick(path)
        })
        dndRV.adapter = adapter
        folderAdapter = adapter

         @RequiresApi(Build.VERSION_CODES.O)
         fun addFolderItem(file: File){
            CoroutineScope(Dispatchers.Main).launch{
                (adapter).addItem(ThumbnailModel(file))
            }
         }

        directorySearcher.getDirectories(::addFolderItem)
    }

    private fun folderItemOnClick(path: String){
        val intent = Intent(mainActivity, ImageGalleryActivity::class.java)
        intent.putExtra("dirPath", path)
        startActivity(intent)
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