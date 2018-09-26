package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.petfeed.petfeed.R
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.PrefManager
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

class SettingActivity : AppCompatActivity() {


    lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white2, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        prefManager = PrefManager(this)
//        personalButton.onClick {
//            startActivity<PersonalSettingActivity>()
//        }
        accountButton.onClick {
            startActivity<AccountSettingActivity>()
        }
        paymentButton.onClick {
            startActivity<PaymentSettingActivity>()
        }
        languageButton.onClick {
            startActivity<LanguageSettingActivity>()
        }
        serviceButton.onClick {
            startActivity<ServiceSettingActivity>()
        }
        infoButton.onClick {
            startActivity<InfoSettingActivity>()
        }
        logOutButton.onClick {
            DataHelper.resetData()
            prefManager.userPassword = ""
            prefManager.userId = ""
            startActivity<LoginActivity>()
        }
        setToolbar()
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
