package com.example.unipicdev.views.activities

import android.os.Bundle
import android.view.GestureDetector
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.unipicdev.R
import com.example.unipicdev.views.adapters.ImageViewerAdapter
import com.veinhorn.scrollgalleryview.ScrollGalleryView
import github.hellocsl.layoutmanager.gallery.GalleryLayoutManager
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File


class ImageActivity : AppCompatActivity()/*, ViewSwitcher.ViewFactory*/{

    private var position = 0
    private var images = arrayListOf<File>()
    private lateinit var mImageSwitcher: ImageSwitcher
    private lateinit var mGestureDetector: GestureDetector
    private lateinit var galleryView: ScrollGalleryView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        initRecyclerView()
    }

    private fun initRecyclerView(){
        val images = intent.getSerializableExtra("imageList") as ArrayList<File>
        val position = intent.getIntExtra("position",0)

        val layoutManager = GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL)
        layoutManager.attach(recyclerView, position)

        val adapter = ImageViewerAdapter(images)
        recyclerView.adapter = adapter
    }

//
//    inner class GestureListener : GestureDetector.OnGestureListener{
//
//        private val SWIPE_MIN_DISTANCE = 120
//        private val SWIPE_MAX_OFF_PATH = 250
//        private val SWIPE_THRESHOLD_VELOCITY = 100
//
//        override fun onDown(e: MotionEvent?): Boolean {
//            return false
//        }
//
//        override fun onShowPress(e: MotionEvent?) {
//
//        }
//
//        override fun onSingleTapUp(e: MotionEvent?): Boolean {
//            return false
//        }
//
//        override fun onScroll(
//            e1: MotionEvent?,
//            e2: MotionEvent?,
//            distanceX: Float,
//            distanceY: Float
//        ): Boolean {
//            return false
//        }
//
//        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
//            try {
//                if (abs(e1!!.y - e2!!.y) > SWIPE_MAX_OFF_PATH) return false
//                // справа налево
//                if (e1.x - e2.x > SWIPE_MIN_DISTANCE
//                        && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    setPositionNext()
//                    showImage(images[position])
//                    Toast.makeText(this@ImageActivity, "Left", Toast.LENGTH_SHORT).show()
//
//                } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE
//                        && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    // слева направо
//                    setPositionPrev()
//                    showImage(images[position])
//                    Toast.makeText(this@ImageActivity, "Right", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                return true
//            }
//            return true
//        }
//    }

//    }
}