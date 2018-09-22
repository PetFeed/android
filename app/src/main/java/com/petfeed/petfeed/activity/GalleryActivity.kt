package com.petfeed.petfeed.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.GlideApp
import com.petfeed.petfeed.GlideRequests
import com.petfeed.petfeed.R
import com.petfeed.petfeed.adapter.GridItemDecoration
import com.petfeed.petfeed.databinding.ItemGalleryBinding
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.ImageUtils
import com.petfeed.petfeed.util.UIUtils
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.item_gallery.view.*


class GalleryActivity : AppCompatActivity() {

    private val items = ArrayList<String>()
    private val selectedArray = ArrayList<Int>()

    var max = 0
    lateinit var requestManager: GlideRequests
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.brown1, true)
        setContentView(R.layout.activity_gallery)

        requestManager = GlideApp.with(this)
        val type = intent.getIntExtra("type", MANY)
        max = when (type) {
            ONLYONE -> 1
            MANY -> 30
            else -> 30
        }
        setToolbar()
        setRecyclerView()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(UIUtils.getIcon(this@GalleryActivity, R.drawable.ic_back, Color.WHITE))
        }
    }

    private fun setRecyclerView() {
        items.addAll(ImageUtils.getPathOfAllImages(this))
        galleyRecyclerView.run {
            layoutManager = GridLayoutManager(this@GalleryActivity, 3)
            adapter = LastAdapter(items, BR.item)
                    .map<String, ItemGalleryBinding>(R.layout.item_gallery) {
                        onBind {
                            it.itemView.run {
                                requestManager
                                        .load(items[it.adapterPosition])
                                        .thumbnail(0.2f)
                                        .into(imageView)

                                if (it.adapterPosition in selectedArray) {
                                    overlayView.visibility = View.VISIBLE
                                    textView.text = "${selectedArray.indexOf(it.adapterPosition) + 1}"
                                } else {
                                    overlayView.visibility = View.GONE
                                    textView.text = ""
                                }
                            }
                        }
                        onClick {
                            if (it.adapterPosition in selectedArray) {
                                selectedArray.remove(it.adapterPosition)
                                adapter?.notifyDataSetChanged()
                            } else {
                                if (selectedArray.size >= max)
                                    selectedArray.removeAt(0)
                                selectedArray.add(it.adapterPosition)
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
            addItemDecoration(GridItemDecoration(3, UIUtils.makeDP(this@GalleryActivity, 3f).toInt()))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.finish_menu -> {
                if (selectedArray.size == 0)
                    setResult(Activity.RESULT_CANCELED)
                else
                    setResult(Activity.RESULT_OK, Intent().putExtra("paths",
                            items
                                    .filterIndexed { index, s -> index in selectedArray }
                                    .toTypedArray()))
                finish()
            }
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val ONLYONE = 1
        val MANY = 2
    }
}
