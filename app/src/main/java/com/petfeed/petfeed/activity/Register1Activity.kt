package com.petfeed.petfeed.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.iid.FirebaseInstanceId
import com.petfeed.petfeed.R
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.UIUtils
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_register1.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject

class Register1Activity : AppCompatActivity() {

    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.white2)
        setContentView(R.layout.activity_register1)
        setToolbar()

        nextButton.onClick {
            setProgressDialog()
            if (!NetworkHelper.checkNetworkConnected(this@Register1Activity)) {
                progressDialog.dismiss()
                UIUtils.printNetworkCaution(this@Register1Activity)
                return@onClick
            }
            if (!checkInput()) {
                progressDialog.dismiss()
                return@onClick
            }

            val id = idEditText.text.toString()
            val pw = pwEditText.text.toString()
            val nickname = nameEditText.text.toString()

            async(CommonPool) { NetworkHelper.retrofitInstance.postRegister(id, pw, nickname, FirebaseInstanceId.getInstance().token!!).execute() }.await().apply {
                progressDialog.dismiss()
                if (!isSuccessful) {
                    return@onClick
                }

                val json: JSONObject = JSONObject(body()!!.string())
                val isSuccess = json.getBoolean("success")
                if (!isSuccess) {
                    toast("이미 존재하는 유저입니다")
                    return@onClick
                }
            }
            toast("회원 가입 성공")
            startActivity<Register2Activity>()
            finish()
        }
    }

    private fun checkInput(): Boolean {
        val idRegex = Regex("[a-zA-z0-9.]+@[a-zA-z0-9]+\\.[a-zA-z0-9.]+")
        if (!idRegex.matches(idEditText.text.toString())) {
            toast("아이디는 이메일 형식으로 입력해주세요")
            return false
        }
        if (pwEditText.text.length > 16 || pwEditText.text.length < 8) {
            toast("비밀번호는 8자리 이상 16자리 이하이어야 합니다.")
            return false
        }
        if (pwEditText.text.toString() != repwEditText.text.toString()) {
            toast("비밀번호 재입력을 확인해 주세요")
            return false
        }
        val nameRegex = Regex("[a-zA-Z가-힣ㅏ-ㅣㄱ-ㅎ0-9_.@]+")
        if (nameEditText.text.isBlank() || !nameRegex.matches(nameEditText.text.toString())) {
            toast("별명을 입력해 주세요")
            return false
        }

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

    private fun setProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("회원가입중...")
        progressDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
