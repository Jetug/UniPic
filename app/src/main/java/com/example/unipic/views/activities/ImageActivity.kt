package com.example.unipic.views.activities

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.unipic.R
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import kotlin.math.*


class ImageActivity : AppCompatActivity(), ViewSwitcher.ViewFactory{

    private var position = 0
    private var images = arrayListOf<File>()
    private lateinit var mImageSwitcher: ImageSwitcher
    private lateinit var mGestureDetector: GestureDetector


    var x1 = 0.0f
    var x2 = 0.0f
    var y1 = 0.0f
    var y2 = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        initImageActivity()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initImageActivity(){
        val intent = intent
        val imagePath: String = intent.getCharSequenceExtra("imagePath") as String
        images = intent.getSerializableExtra("imageList") as ArrayList<File>

        for (i in 0 until images.size){
            if(images[i].absolutePath == imagePath) {
                position = i
                break
            }
        }

        mImageSwitcher = findViewById<View>(R.id.imageSwitcher) as ImageSwitcher
        mImageSwitcher.setFactory(this)

//        val inAnimation: Animation = AlphaAnimation(0.0f, 1.0f)
//        inAnimation.duration = 2000
//        val outAnimation: Animation = AlphaAnimation(1.0f, 0.0f)
//        outAnimation.duration = 2000

//        val inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
//        val outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
//
//        mImageSwitcher.inAnimation = inAnimation
//        mImageSwitcher.outAnimation = outAnimation

        showImage(images[position])

        //mGestureDetector = GestureDetector(this, GestureListener())
    }

    private fun showImage(file: File){
        Glide.with(this)
            .load(file)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                        e: GlideException?, model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(
                        resource: Drawable?, model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (position + 1 == images.size) {
                        position = 0
                    }
                    imageSwitcher.setImageDrawable(resource)
                    return true
                }
            })
            .into(mImageSwitcher.currentView as PhotoView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun makeView(): View {
        val photoView = PhotoView(this)

        photoView.layoutParams = FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        photoView.setBackgroundColor(-0x1000000)
        photoView.isClickable = true
//        photoView.setOnTouchListener { _, event ->
//            //mGestureDetector.onTouchEvent(event)
//            onTouchEvent(event)
//        }
        return photoView
    }

    inner class GestureListener : GestureDetector.OnGestureListener{

        private val SWIPE_MIN_DISTANCE = 120
        private val SWIPE_MAX_OFF_PATH = 250
        private val SWIPE_THRESHOLD_VELOCITY = 100

        override fun onDown(e: MotionEvent?): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent?) {

        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent?) {

        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            try {
                if (abs(e1!!.y - e2!!.y) > SWIPE_MAX_OFF_PATH) return false
                // справа налево
                if (e1.x - e2.x > SWIPE_MIN_DISTANCE
                        && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    setPositionNext()
                    showImage(images[position])
                    Toast.makeText(this@ImageActivity, "Left", Toast.LENGTH_SHORT).show()

                } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE
                        && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // слева направо
                    setPositionPrev()
                    showImage(images[position])
                    Toast.makeText(this@ImageActivity, "Right", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                return true
            }
            return true
        }
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.buttonForward -> {
                setPositionNext()
                showImage(images[position])
            }
            R.id.buttonPrev -> {
                setPositionPrev()
                showImage(images[position])
            }
            else -> {
            }
        }
    }

    fun setPositionNext() {
        position++
        if (position > images.size - 1) {
            position = 0
        }
    }

    fun setPositionPrev() {
        position--
        if (position < 0) {
            position = images.size - 1
        }
    }
}