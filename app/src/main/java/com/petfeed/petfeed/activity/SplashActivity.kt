package com.petfeed.petfeed.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.petfeed.petfeed.R
import com.petfeed.petfeed.model.User
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.util.PrefManager
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.startActivity
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {


    lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ActivityUtils.statusBarSetting(window, this, R.color.brown1, true)

        prefManager = PrefManager(this)

        if (prefManager.userId != "" && prefManager.userPassword != "" && NetworkHelper.checkNetworkConnected(this)) {
            async(CommonPool) { getUser() }
        } else {
            startLoginActivity()
        }
    }

    private fun startLoginActivity() {
        Handler().postDelayed({
            startActivity<LoginActivity>()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 1000)
    }

    private suspend fun getUser() {
        val fcmToken = FirebaseInstanceId.getInstance().token!!
        async(CommonPool) { NetworkHelper.retrofitInstance.postLogin(prefManager.userId, prefManager.userPassword, fcmToken).execute() }.await().apply {
            if (!isSuccessful) {
                startLoginActivity()
                return
            }

            val json: JSONObject = JSONObject(body()!!.string())
            val isSuccess = json.getBoolean("success")

            if (!isSuccess) {
                startLoginActivity()
                return
            }

            DataHelper.datas?.token = json.getString("token")
        }
        async(CommonPool) { NetworkHelper.retrofitInstance.getUser(DataHelper.datas!!.token).execute() }.await().apply {
            if (!isSuccessful) {
                startLoginActivity()
                return
            }
            val json: JSONObject = JSONObject(body()!!.string())
            val isSuccess = json.getBoolean("success")

            if (!isSuccess) {
                startLoginActivity()
                return
            }

            val user = Gson().fromJson(json.getString("user"), User::class.java)
            DataHelper.datas?.user = user
        }

        startActivity<MainActivity>()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
