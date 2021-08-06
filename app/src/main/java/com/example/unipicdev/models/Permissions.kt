package com.example.unipicdev.models

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.unipicdev.appContext
import com.example.unipicdev.currentActivity

fun isStoragePermissionGranted(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (appContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && appContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
            Log.v("My", "Permission is granted")
            true
        } else {
            Log.v("My", "Permission is revoked")
            ActivityCompat.requestPermissions(
                currentActivity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                1
            )
            false
        }
    } else { //permission is automatically granted on sdk<23 upon installation
        Log.v("My", "Permission is granted")
        true
    }
}
