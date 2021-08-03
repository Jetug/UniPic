package com.example.unipicdev.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.alexvasilkov.gestures.animation.ViewPositionAnimator.PositionUpdateListener
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter
import com.alexvasilkov.gestures.transition.GestureTransitions
import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator
import com.alexvasilkov.gestures.transition.tracker.SimpleTracker
import com.example.unipicdev.R
import com.example.unipicdev.models.MediaSearcher
import com.example.unipicdev.models.ThumbnailModel
import com.example.unipicdev.models.room.saveMediaSorting
import com.example.unipicdev.models.interfaces.ItemOnClickListener
import com.example.unipicdev.views.adapters.MediaAdapter
import com.example.unipicdev.views.adapters.OnPaintingClickListener
import com.example.unipicdev.views.adapters.PagerAdapter
import com.example.unipicdev.views.dialogs.SortingDialog
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import kotlinx.android.synthetic.main.activity_image_gallery.*
import java.io.File

class MediaGalleryActivity : BaseActivity(){

    private val mediaSearcher = MediaSearcher()
    private val colCount = 3
    private val imageGalleryActivity = this

    private lateinit var imageAdapter: MediaAdapter
    private lateinit var dirPath: String

    private lateinit var list: RecyclerView
    private lateinit var pager: ViewPager
    private lateinit var background: View

    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var animator: ViewsTransitionAnimator<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_gallery)
        initImageRV()
        initViewPager()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_media_gallery, menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(menu != null){
            val sw = menu.findItem(R.id.positionCB).actionView?.findViewById<SwitchMaterial>(R.id.positionCB)
            sw?.setOnCheckedChangeListener(::onChecked)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.sorting -> sort()
            R.id.checkMode -> enableSelectionMode()
//            R.id.byName -> imageAdapter.sort(SortingType.NAME)
//            R.id.byCreationDate -> imageAdapter.sort(SortingType.CREATION_DATE)
//            R.id.byModificationDate -> imageAdapter.sort(SortingType.MODIFICATION_DATE)
//            R.id.custom -> imageAdapter.sort(SortingType.CUSTOM)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {

//        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun initImageRV(){
        val linearLayoutManager = GridLayoutManager(applicationContext, colCount)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val imagesDRV = findViewById<RecyclerView>(R.id.imagesDRV)
        imagesDRV.layoutManager = linearLayoutManager
        imagesDRV.setHasFixedSize(true)

        list = imagesDRV
        //imagesDRV.orientation = DragDropSwipeRecyclerView.ListOrientation.HORIZONTAL_LIST_WITH_UNCONSTRAINED_DRAGGING

        dirPath = intent.getCharSequenceExtra("dirPath")as String
        val imageFiles = mediaSearcher.getImageFiles(dirPath)
        val imagesList = mutableListOf<ThumbnailModel>()

        for (file in imageFiles)
        {
            imagesList.add(ThumbnailModel(file))
        }

        val size: DisplayMetrics = getDisplaySize(this)
        val width = size.widthPixels / colCount
        val adapter = MediaAdapter(this, imagesList, width, dirPath, object : ItemOnClickListener {
            override fun onClick(path: String, position: Int) {
                val imageActivityIntent = Intent(imageGalleryActivity, MediaViewerActivity::class.java)
                val buffList = mutableListOf<File>()

                for (file in imageAdapter.files)
                {
                    buffList.add(file.file)
                }

                var pos = 0
                for ((i, file) in buffList.withIndex()){
                    if (file.absolutePath == path){
                        pos = i
                        break
                    }
                }

                onPaintingClick2(position)
                imageActivityIntent.putExtra("imagePath", path)
                imageActivityIntent.putExtra("position", pos)
                imageActivityIntent.putExtra("imageList", ArrayList<File>(buffList))

                startActivity(imageActivityIntent)
            }
        },
        object: OnPaintingClickListener{
            override fun onPaintingClick(position: Int) {
                onPaintingClick2(position)
            }
        })



        imagesDRV.adapter = adapter
        imageAdapter = adapter

        ////////////////////////////////////////////////////////////////////////////////////

        pager = findViewById(R.id.recycler_pager)
        pagerAdapter = PagerAdapter(pager, listOf())
        pager.adapter = pagerAdapter
        pager.pageMargin = 12


        val listTracker: SimpleTracker = object : SimpleTracker() {
            override fun getViewAt(position: Int): View? {
                val holder: RecyclerView.ViewHolder? = list.findViewHolderForLayoutPosition(position)
                return if (holder == null) null else MediaAdapter.getImageView(holder)
            }
        }

        val pagerTracker: SimpleTracker = object : SimpleTracker() {
            override fun getViewAt(position: Int): View? {
                val holder: PagerAdapter.ViewHolder? = pagerAdapter.getViewHolder(position)
                return if (holder == null) null else PagerAdapter.getImageView(holder)
            }
        }

        animator = GestureTransitions.from(list, listTracker).into(pager, pagerTracker)

        background = findViewById(R.id.recycler_full_background)
        animator.addPositionUpdateListener { pos: Float, _: Boolean ->
            applyImageAnimationState(
                pos
            )
        }

    }

    private fun onPaintingClick2(position: Int) {
        // Animating image transition from given list position into pager
        animator.enter(position, true)
    }

    private fun applyImageAnimationState(position: Float) {
        background.visibility = if (position == 0f) View.INVISIBLE else View.VISIBLE
        background.alpha = position
    }

    private fun initViewPager(){}

    private fun sort(){
        val dialog = SortingDialog(false){
                sorting, order ->
            imageAdapter.sort(sorting, order)

            CoroutineScope(Dispatchers.Default).launch{
                saveMediaSorting(dirPath, sorting, order)
            }
        }
        dialog.show(supportFragmentManager, "SortingDialog")
    }

    private fun enableSelectionMode(){
        imageAdapter.selectionMode = !imageAdapter.selectionMode
    }

    private fun onChecked(buttonView: CompoundButton, isChecked: Boolean){
        imageAdapter.isDragEnabled = isChecked
    }
}