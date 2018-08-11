package com.petfeed.petfeed.Util

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.petfeed.petfeed.R
import com.petfeed.petfeed.View.BackdropTopView
import org.jetbrains.anko.displayMetrics

class BackdropHelper(val mContext: Context, val topView: BackdropTopView, val contentView: View, val dummyView: View) {
    val topViewHeight = topView.height
    val contentViewHeight = contentView.height
    val animator: ValueAnimator = ValueAnimator.ofInt().apply {
        duration = 300
        addUpdateListener {
            mMargin = it.animatedValue as Int
        }
    }
    val contentParams = contentView.layoutParams as RelativeLayout.LayoutParams
    val topViewParmas = topView.layoutParams as RelativeLayout.LayoutParams

    init {
        setScrollListener()

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                isScroll = false
            }

            override fun onAnimationCancel(p0: Animator?) {
                isScroll = false
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
    }

    var isScroll = false
    var mMargin = 0
        set(value) {
            val ratio = value.toFloat() / (contentViewHeight - topViewHeight)
            val horizontalMargin = ratio * (16 * mContext.displayMetrics.density)

            contentParams.setMargins(horizontalMargin.toInt(), value, horizontalMargin.toInt(), 0)
            contentView.run {
                layoutParams = contentParams
                alpha = 1 - ratio
            }
            topViewParmas.run {
                setMargins(horizontalMargin.toInt(), 0, horizontalMargin.toInt(), 0)
                height = if (value != 0) contentParams.height else topViewHeight
            }
            topView.run {
                layoutParams = topViewParmas
                outerColor = ratioARGB(ContextCompat.getColor(mContext, R.color.colorPrimaryDark), 1 - ratio)
                innerColor = ratioARGB(Color.parseColor("#4d442d26"), ratio)
                isInnerSolid = value != 0
                radius = (16 * mContext.displayMetrics.density) - ratio * (4 * mContext.displayMetrics.density)
            }
            dummyView.alpha = 0.3f * (1 - ratio)
            field = value
        }

    private fun setScrollListener() {
        contentView.setOnTouchListener { _, e ->
            if (!isScroll)
                return@setOnTouchListener false
            val currentTopMargin = contentParams.topMargin + e.y.toInt()
            when (e.action) {
                MotionEvent.ACTION_UP -> {
                    isScroll = false
                    if (currentTopMargin < contentViewHeight / 2)
                        up()
                    else {
                        down()
                    }
                    if (currentTopMargin > mMargin + contentViewHeight / 200) {
                        down()
                    }
                    if (currentTopMargin < mMargin - contentViewHeight / 200) {
                        up()
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    mMargin = when {
                        currentTopMargin < 0 -> 0
                        currentTopMargin > contentViewHeight - topViewHeight -> contentViewHeight - topViewHeight
                        else -> currentTopMargin
                    }
                }
            }
            true
        }

        topView.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    isScroll = true
                }
                MotionEvent.ACTION_UP -> {
                    isScroll = false
                }
            }
            false
        }
    }

    fun down() {
        animator.setIntValues(mMargin, contentViewHeight - topViewHeight)
        animator.start()
    }

    fun up() {
        animator.setIntValues(mMargin, 0)
        animator.start()
    }

    fun ratioARGB(color: Int, ratio: Float): Int {
        val a = Color.alpha(color) * ratio
        val r = Color.red(color) * ratio
        val g = Color.green(color) * ratio
        val b = Color.blue(color) * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }
}