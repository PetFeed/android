package com.petfeed.petfeed.util

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import org.jetbrains.anko.displayMetrics
import java.text.SimpleDateFormat
import java.util.*

object UIUtils {
    fun makeDP(context: Context, dp: Float): Float = context.displayMetrics.density * dp

    fun ratioARGB(color: Int, ratio: Float): Int {
        val a = Color.alpha(color) * ratio
        val r = Color.red(color) * ratio
        val g = Color.green(color) * ratio
        val b = Color.blue(color) * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }

    fun getIcon(context: Context, rid: Int, color: Int = 0, colorId: Int = 0): Drawable =
            ContextCompat.getDrawable(context, rid)!!.apply {
                val realColor = if (colorId != 0) ContextCompat.getColor(context, colorId) else color
                setColorFilter(realColor, PorterDuff.Mode.SRC_ATOP)
            }

    fun getLaterText(date: Date): String {
        val diff = System.currentTimeMillis() - date.time
        val pattern: String = when {
            diff > 1000L * 60 * 60 * 24 * 30 * 365 -> "y년 전"
            diff > 1000L * 60 * 60 * 24 * 30 -> "M월 전"
            diff > 1000L * 60 * 60 * 24 -> "d일 전"
            diff > 1000L * 60 * 60 -> "k시간 전"
            diff > 1000L * 60 -> "m분 전"
            diff > 1000L -> "s초 전"
            else -> ""
        }
        return SimpleDateFormat(pattern).format(diff)
    }
}
