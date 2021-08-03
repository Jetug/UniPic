package com.example.unipicdev.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unipicdev.currentActivity

open class BaseActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentActivity = this
    }
}