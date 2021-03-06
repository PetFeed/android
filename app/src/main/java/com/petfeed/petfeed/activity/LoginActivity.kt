package com.petfeed.petfeed.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.petfeed.petfeed.R
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.model.User
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.PrefManager
import com.petfeed.petfeed.util.UIUtils
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    lateinit var prefManager: PrefManager
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.white2)
        setContentView(R.layout.activity_login)

        prefManager = PrefManager(this@LoginActivity)

        loginButton.onClick {
            if (!NetworkHelper.checkNetworkConnected(this@LoginActivity)) {
                UIUtils.printNetworkCaution(this@LoginActivity)
                return@onClick
            }
            if (!checkInput())
                return@onClick

            setProgressDialog()
            val id = idEditText.text.toString()
            val pw = pwEditText.text.toString()
            val fcmToken = FirebaseInstanceId.getInstance().token!!
            async(CommonPool) { NetworkHelper.retrofitInstance.postLogin(id, pw, fcmToken).execute() }.await().apply {
                if (!isSuccessful) {
                    progressDialog.dismiss()
                    return@onClick
                }

                val json: JSONObject = JSONObject(body()!!.string())
                val isSuccess = json.getBoolean("success")

                if (!isSuccess) {
                    val message = json.getString("message")
                    toast(message)
                    progressDialog.dismiss()
                    return@onClick
                }

                DataHelper.datas?.token = json.getString("token")
            }

            prefManager.userId = id
            prefManager.userPassword = pw

            async(CommonPool) { NetworkHelper.retrofitInstance.getUser(DataHelper.datas!!.token).execute() }.await().apply {
                progressDialog.dismiss()
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

    private fun setProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("로그인 중")
        progressDialog.show()
    }

    private fun checkInput(): Boolean {
        if (idEditText.text.toString().isEmpty())
            return false
        if (pwEditText.text.toString().isEmpty())
            return false
        return true
    }
}
