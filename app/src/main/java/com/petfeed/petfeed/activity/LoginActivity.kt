package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.petfeed.petfeed.R
import com.petfeed.petfeed.model.User
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.DataHelper
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.white2)
        setContentView(R.layout.activity_login)

        loginButton.onClick {
            if (!checkInput())
                return@onClick

            val id = idEditText.text.toString()
            val pw = pwEditText.text.toString()

            var userToken = ""
            async(CommonPool) { NetworkHelper.retrofitInstance.postLogin(id, pw).execute() }.await().apply {
                if (!isSuccessful)
                    return@onClick

                val json: JSONObject = JSONObject(body()!!.string())
                val isSuccess = json.getBoolean("success")

                if (!isSuccess)
                    return@onClick

                userToken = json.getString("token")
            }

            async(CommonPool) { NetworkHelper.retrofitInstance.getUser(userToken).execute() }.await().apply {
                if (!isSuccessful)
                    return@onClick

                val json: JSONObject = JSONObject(body()!!.string())
                val isSuccess = json.getBoolean("success")

                if (!isSuccess)
                    return@onClick

                val user = Gson().fromJson(json.getString("user"), User::class.java)
                DataHelper.datas?.user = user
            }
            startActivity<MainActivity>()
            finishAffinity()
        }
        containerRegister.onClick {
            startActivity<Register1Activity>()
        }
    }

    private fun checkInput(): Boolean {
        if (idEditText.text.isEmpty())
            return false
        if (pwEditText.text.isEmpty())
            return false
        return true
    }
}
