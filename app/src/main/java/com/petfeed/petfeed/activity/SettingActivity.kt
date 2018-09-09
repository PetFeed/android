package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.petfeed.petfeed.R
import com.petfeed.petfeed.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white2, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

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
}
