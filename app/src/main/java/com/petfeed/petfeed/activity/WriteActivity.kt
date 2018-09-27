package com.petfeed.petfeed.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.ImageView
import com.android.databinding.library.baseAdapters.BR
import com.bumptech.glide.Glide
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.GlideApp
import com.petfeed.petfeed.R
import com.petfeed.petfeed.databinding.ItemWriteAddBinding
import com.petfeed.petfeed.databinding.ItemWriteCardBinding
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.UIUtils
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_write.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.io.File
import java.net.SocketTimeoutException


class WriteActivity : AppCompatActivity() {

    val imageArray: ArrayList<Any> = ArrayList()
    val GALLERY_CODE = 1
    val PERMISSION_CODE = 2
    var isWriting = false
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.white2, false)
        setContentView(R.layout.activity_write)
        setToolbar()
        setRecyclerView()

        GlideApp.with(this)
                .load(NetworkHelper.url + DataHelper.datas!!.user.profile)
                .into(profileImage)

        userName.text = DataHelper.datas!!.user.nickname
        writeButton.onClick {
            if (!NetworkHelper.checkNetworkConnected(this@WriteActivity)) {
                UIUtils.printNetworkCaution(this@WriteActivity)
                return@onClick
            }
            if (!checkData()) {
                toast("내용과 사진이 필요합니다.")
                return@onClick
            }
            setProgressDialog()
            val pictures = ArrayList<MultipartBody.Part>()
            imageArray.filter { it is String }.forEach {
                val file = File(it as String)
                val body = RequestBody.create(MediaType.parse("image/*"), file)
                pictures.add(MultipartBody.Part.createFormData("pictures", file.name, body))
            }
            val contents = RequestBody.create(MediaType.parse("text/plain"), contents.text.toString())
            val hashTags = ArrayList<RequestBody>()
            val regex = Regex("#[a-zA-z_가-힣ㄱ-ㅎㅏ-ㅣ]+")
            regex.findAll(this@WriteActivity.contents.text.toString()).forEach {
                hashTags.add(RequestBody.create(MediaType.parse("text/plain"), it.value))
//                hashTags.add(it.value)
            }
            try {
                async(CommonPool) { NetworkHelper.retrofitInstance.postBoard(DataHelper.datas!!.token, pictures.toTypedArray(), contents, hashTags.toTypedArray()).execute() }
                        .await().apply {
                            if (isSuccessful)
                                finish()
                            else {
                                toast("오류가 발생했습니다.")
                            }
                        }
            } catch (e: SocketTimeoutException) {
                progressDialog.dismiss()
                e.printStackTrace()
                toast("네트워크 오류가 발생했습니다.")
            }
            progressDialog.dismiss()
            isWriting = false
        }
    }

    private fun setRecyclerView() {
        imageArray.add(0)
        imageRecyclerView.run {
            layoutManager = LinearLayoutManager(this@WriteActivity, LinearLayoutManager.HORIZONTAL, false)
            LastAdapter(imageArray, BR.item)
                    .map<Int, ItemWriteAddBinding>(R.layout.item_write_add) {
                        onBind {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                it.itemView.findViewById<ImageView>(R.id.imageContainer).backgroundResource = R.drawable.ripple
                            }
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
                            it.itemView.findViewById<ImageView>(R.id.imageContainer).backgroundColor = Color.TRANSPARENT
                            it.adapterPosition
                            Glide.with(context)
                                    .load(imageArray[it.adapterPosition])
                                    .into(it.binding.image)
                        }
                        onLongClick {
                            imageArray.removeAt(it.adapterPosition)
                            imageRecyclerView.adapter?.notifyItemRemoved(it.adapterPosition)
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

    private fun setProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("게시 중 입니다.")
        progressDialog.show()
    }

    private fun checkData(): Boolean =
            contents.text.toString().isNotBlank() && imageArray.size > 1


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
