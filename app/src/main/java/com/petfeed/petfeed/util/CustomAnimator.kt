package com.petfeed.petfeed.util

import android.os.Handler

class CustomAnimator {
    var duration: Long = 0L
    var startValue: Int = 0
    var endValue: Int = 0
    val handler = Handler()

    private var nowValue: Int = 0
    var onAnimationUpdate: (Int) -> Unit = {}
    var onAnimationEnd: () -> Unit = {}
    var onAnimationCancel: () -> Unit = {}
    var isRunning = false

    fun start() {
        if (isRunning)
            stop()
        animate()
        isRunning = true
    }

    fun stop() {
        if (!isRunning)
            return
        handler.removeCallbacksAndMessages(null)
        isRunning = false
        onAnimationCancel.invoke()
    }

    private fun animate() {
        isRunning = true
        val max = duration / 16
        val diff = endValue - startValue
        (1..max).forEach {
            handler.postDelayed({
                nowValue = startValue + (diff * (it / max.toFloat())).toInt()
                onAnimationUpdate.invoke(nowValue)
                if (it == max) {
                    isRunning = false
                    onAnimationEnd.invoke()
                }
            }, (duration / max) * it)
        }
    }
}