package com.example.unipic.views.acivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.unipic.R
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

            initImageActivity()
    }

    private fun initImageActivity(){
        val intent = intent
        val imagePath: String = intent.getCharSequenceExtra("imagePath") as String
        imageView.setImageBitmap(imageCreator.getBitmap(imagePath))
    }
}