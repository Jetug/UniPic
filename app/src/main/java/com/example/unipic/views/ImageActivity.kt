package com.example.unipic.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.unipic.R
import com.example.unipic.models.MediaSearcher
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        CoroutineScope(Dispatchers.Default).launch {
            initImageActivity()
        }
    }

    private fun initImageActivity(){
        val intent = intent
        val imagePath: String = intent.getCharSequenceExtra("imagePath") as String
        imageView.setImageBitmap(imageCreator.getBitmap(imagePath))
    }
}