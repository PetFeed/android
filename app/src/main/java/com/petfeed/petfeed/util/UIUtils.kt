package com.petfeed.petfeed.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.widget.Toast
import org.jetbrains.anko.displayMetrics
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


    val times = arrayOf(60, 60, 24, 30, 12)

    @SuppressLint("SimpleDateFormat")
    fun getLaterText(date: Date): String {
        var i = 0
        var diff = (System.currentTimeMillis() - date.time) / 1000
        val checkTime: () -> Boolean = {
            diff /= times[i++]
            diff < times[i]
        }

        return if (diff < 60) {
            "방금 전"
        } else if (checkTime.invoke()) {
            "$diff 분 전"
        } else if (checkTime.invoke()) {
            "$diff 시간 전"
        } else if (checkTime.invoke()) {
            "$diff 일 전"
        } else if (checkTime.invoke()) {
            "$diff 달 전"
        } else {
            "$diff 년 전"
        }
    }

    fun printNetworkCaution(context: Context) {
        Toast.makeText(context, "네트워크 상태를 확인해주세요", Toast.LENGTH_SHORT).show()
    }

    fun printServiceNotYet(context: Context) {
        Toast.makeText(context, "준비중인 서비스 입니다", Toast.LENGTH_SHORT).show()
    }
}
