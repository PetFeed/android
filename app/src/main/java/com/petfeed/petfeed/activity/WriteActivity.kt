package com.petfeed.petfeed.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.android.databinding.library.baseAdapters.BR
import com.bumptech.glide.Glide
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.R
import com.petfeed.petfeed.databinding.ItemWriteAddBinding
import com.petfeed.petfeed.databinding.ItemWriteCardBinding
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.UIUtils
import kotlinx.android.synthetic.main.activity_write.*
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult


class WriteActivity : AppCompatActivity() {

    val imageArray: ArrayList<Any> = ArrayList()
    val GALLERY_CODE = 1
    val PERMISSION_CODE = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.white2, false)
        setContentView(R.layout.activity_write)
        setToolbar()
        setRecyclerView()
        writeButton.onClick {
            finish()
        }
    }

    private fun setRecyclerView() {
        imageArray.add(0)
        imageRecyclerView.run {
            layoutManager = LinearLayoutManager(this@WriteActivity, LinearLayoutManager.HORIZONTAL, false)
            LastAdapter(imageArray, BR.item)
                    .map<Int, ItemWriteAddBinding>(R.layout.item_write_add) {
                        onBind {
                            it.binding.image.layoutParams = it.binding.image.layoutParams.apply {
                                width = UIUtils.makeDP(this@WriteActivity, 24f).toInt()
                                height = UIUtils.makeDP(this@WriteActivity, 24f).toInt()
                            }
                            it.binding.image.imageResource = R.drawable.ic_add
                        }
                        onClick {
                            requestPermission()
                        }
                    }
                    .map<String, ItemWriteCardBinding>(R.layout.item_write_card) {
                        onBind {
                            it.adapterPosition
                            Glide.with(context)
                                    .load(imageArray[it.adapterPosition])
                                    .into(it.binding.image)
                        }
                    }
                    .into(this)
        }
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    private fun getPictureOnGallery() {
        startActivityForResult<GalleryActivity>(GALLERY_CODE)
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_CODE)
        } else {
            getPictureOnGallery()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPictureOnGallery()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_CODE) {
            imageArray.addAll(data!!.getStringArrayExtra("paths"))
            imageRecyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_down)
    }
}
