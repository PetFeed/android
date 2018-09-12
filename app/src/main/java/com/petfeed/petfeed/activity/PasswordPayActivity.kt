package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.petfeed.petfeed.R
import com.petfeed.petfeed.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_password_pay.*
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.collections.ArrayList

class PasswordPayActivity : AppCompatActivity() {

    var password: String = ""
        set(value) {
            field = value
            onChangePassword()

        }
    val keyList = ArrayList<TextView>()
    val numList = ArrayList<ImageView>()
    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white2)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_pay)

        startActivity<ChargeLuvPayActivity>()

        initData()
        generateKey()
        backspace.onClick {
            password = password.slice(0 until password.length - 1)
        }
    }

    private fun initData() {
        keyList.run {
            add(key1)
            add(key2)
            add(key3)
            add(key4)
            add(key5)
            add(key6)
            add(key7)
            add(key8)
            add(key9)
            add(key10)
            add(key11)
        }
        numList.run {
            add(num1)
            add(num2)
            add(num3)
            add(num4)
            add(num5)
            add(num6)
        }
    }

    private fun generateKey() {
        keyList.run {
            keyList.forEach {
                it.onClick { _ ->
                    if (password.length < 6)
                        password += it.text
                }
            }

            (0 until 11).forEach {
                removeAt(Random().nextInt(11 - it)).apply {
                    text = if (it in 0..8) (it + 1).toString() else ""
                    if (it == 9)
                        backgroundColorResource = R.color.brown1
                }
            }
        }
    }

    private fun onChangePassword() {
        numList.forEachIndexed { i, imageView ->
            imageView.imageResource = if (i in 0 until password.length) R.drawable.brown1_solid_radius
            else android.R.color.transparent
        }
    }
}
