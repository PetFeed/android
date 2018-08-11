package com.petfeed.petfeed.util

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.Window
import android.view.WindowManager

object ActivityUtils {
    fun statusBarSetting(window: Window, context: Context? = null, colorId: Int = 0) {
        window.run {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                context?.let {
                    if (colorId != 0)
                        statusBarColor = ContextCompat.getColor(it, colorId)
                }
            }
        }
    }
}