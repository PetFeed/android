package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.petfeed.petfeed.R
import com.petfeed.petfeed.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_register1.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

class Register1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.white2)
        setContentView(R.layout.activity_register1)

        button_register1_next.onClick {
            startActivity<Register2Activity>()
        }
    }
}
