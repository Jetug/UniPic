package com.example.unipic.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unipic.R
import com.example.unipic.models.MediaSearcher
import com.example.unipic.models.interfaces.ItemOnClickListener
import com.example.unipic.views.adapters.FolderAdapter
import ir.androidexception.filepicker.dialog.DirectoryPickerDialog
import ir.androidexception.filepicker.interfaces.OnCancelPickerDialogListener
import ir.androidexception.filepicker.interfaces.OnConfirmDialogListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener(::clickFun)
        testBtn.setOnClickListener(:: clickTest)

        val linearLayoutManager = GridLayoutManager(applicationContext, 3)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        foldersRV.layoutManager = linearLayoutManager
        foldersRV.setHasFixedSize(true)


        val intent = Intent(this, ImageGalleryActivity::class.java)

        val path = "/storage/emulated/0/pixiv/"
        val folder = File(path)
        //val folderList = mediaSearcher.getFolderFiles()

        CoroutineScope(Dispatchers.Main).launch {
            val folderList = initFolderRV()
            //delay(60000)
            foldersRV.adapter = FolderAdapter(folderList, object : ItemOnClickListener {
                override fun onClick(path: String) {
                    intent.putExtra("dirPath", path)
                    startActivity(intent)
                }
            })
        }
    }

    private val mediaSearcher = MediaSearcher()

    private fun clickTest(v: View){

    }

    suspend fun initFolderRV(): ArrayList<File>{
        return mediaSearcher.getFolderFiles()
    }

    private fun clickFun(v: View) {
        if (permissionGranted()) {
            val directoryPickerDialog = DirectoryPickerDialog(
                    this,
                    OnCancelPickerDialogListener {
                        Toast.makeText(
                                this@MainActivity,
                                "Canceled!!",
                                Toast.LENGTH_SHORT
                        ).show()
                    },
                    OnConfirmDialogListener { files: Array<File> ->
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