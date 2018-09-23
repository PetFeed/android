package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.gson.Gson
import com.petfeed.petfeed.R
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.model.User
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File

class AccountSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white2, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        setToolbar()
//
//        doneButton.onClick {
//            if(!checkInput())
//                return@onClick
//
//            val userToken = DataHelper.datas!!.token
//            val pw = pwEditText.text.toString()
//            val options = HashMap<String, String>()
//            options["user_pw"] = pw
//
//            val file = RequestBody.create(MultipartBody.FORM, "")
//            val body = MultipartBody.Part.createFormData("file", "", file)
//            async(CommonPool){NetworkHelper.retrofitInstance.updateUser(userToken, body, options).execute()}.await().apply {
//                if (!isSuccessful)
//                    return@onClick
//
//                val json: JSONObject = JSONObject(body()!!.string())
//                val isSuccess = json.getBoolean("success")
//
//                if (!isSuccess)
//                    return@onClick
//
//                toast("비밀번호가 변경되었습니다.")
//                finish()
//            }
//        }
    }

    fun checkInput(): Boolean {
        val pw = pwEditText.text.toString()
        val repw = repwEditText.text.toString()

        return pw.isNotBlank() || repw.isNotBlank() || pw == repw
    }


    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
