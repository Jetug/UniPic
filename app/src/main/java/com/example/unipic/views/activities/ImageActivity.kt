package com.example.unipic.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unipic.R
import kotlinx.android.synthetic.main.activity_image.*

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