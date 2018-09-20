package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.iid.FirebaseInstanceId
import com.petfeed.petfeed.R
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_register1.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONObject

class Register1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.white2)
        setContentView(R.layout.activity_register1)
        setToolbar()

        nextButton.onClick {
            if (!checkInput())
                return@onClick

            val id = idEditText.text.toString()
            val pw = pwEditText.text.toString()
            val nickname = nameEditText.text.toString()

            async(CommonPool) { NetworkHelper.retrofitInstance.postRegister(id, pw, nickname, FirebaseInstanceId.getInstance().token!!).execute() }.await().apply {
                if (!isSuccessful)
                    return@onClick

                val json: JSONObject = JSONObject(body()!!.string())
                val isSuccess = json.getBoolean("success")
                if (!isSuccess)
                    return@onClick
            }
            finish()
        }
    }

    private fun checkInput(): Boolean {
        if (idEditText.text.isEmpty())
            return false
        if (pwEditText.text.isEmpty())
            return false
        if (pwEditText.text.toString() != repwEditText.text.toString())
            return false
        if (nameEditText.text.isEmpty())
            return false
        return true
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
