package com.petfeed.petfeed.util

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.view.WindowManager

object ActivityUtils {
    fun statusBarSetting(window: Window, context: Context? = null, colorId: Int = 0, iconWhite: Boolean = false, color: Int? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.run {
                decorView.systemUiVisibility =
                        if (iconWhite)
                            View.SYSTEM_UI_FLAG_VISIBLE
                        else
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                context?.let {
                    if (colorId != 0)
                        statusBarColor = ContextCompat.getColor(it, colorId)
                    if (color != null)
                        statusBarColor = color
                }
            }
        }
    }
}