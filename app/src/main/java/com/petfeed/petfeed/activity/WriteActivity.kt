package com.petfeed.petfeed.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.support.media.ExifInterface
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import com.android.databinding.library.baseAdapters.BR
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.R
import com.petfeed.petfeed.databinding.ItemWriteCardBinding
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.ImageUtils
import com.petfeed.petfeed.util.UIUtils
import kotlinx.android.synthetic.main.activity_write.*
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.IOException


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
        write_button.onClick {
            finish()
        }
    }

    private fun setRecyclerView() {
        imageArray.add("head")
        image_recycler_view.run {
            layoutManager = LinearLayoutManager(this@WriteActivity, LinearLayoutManager.HORIZONTAL, false)
            LastAdapter(imageArray, BR.item)
                    .map<String, ItemWriteCardBinding>(R.layout.item_write_card) {
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
                    .map<Bitmap, ItemWriteCardBinding>(R.layout.item_write_card) {
                        onBind {
                            it.binding.image.imageBitmap = imageArray[it.adapterPosition] as Bitmap
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
        startActivityForResult(Intent(Intent.ACTION_PICK).apply {
            data = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            type = "image/*"
        }, GALLERY_CODE)
    }

    fun makePicture(uri: Uri) {
        val imagePath = ImageUtils.getRealPathFromUri(this, uri)
        Log.e("asdf", "${imagePath}")
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(imagePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val exifOrientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                ?: 0
        val exifDegree = ImageUtils.exifOrientationToDegrees(exifOrientation)

        val bitmap = BitmapFactory.decodeFile(imagePath)
        imageArray.add(ImageUtils.rotate(bitmap, exifDegree.toFloat()))
        image_recycler_view.adapter!!.notifyDataSetChanged()
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
            makePicture(data?.data!!)
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
