package com.petfeed.petfeed.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import org.jetbrains.anko.displayMetrics

object UIUtils {
    fun makeDP(context: Context, dp: Float): Float = context.displayMetrics.density * dp

    fun ratioARGB(color: Int, ratio: Float): Int {
        val a = Color.alpha(color) * ratio
        val r = Color.red(color) * ratio
        val g = Color.green(color) * ratio
        val b = Color.blue(color) * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }
}