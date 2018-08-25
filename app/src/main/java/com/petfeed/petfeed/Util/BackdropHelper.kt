package com.petfeed.petfeed.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.petfeed.petfeed.view.RoundedRecyclerView
import org.jetbrains.anko.displayMetrics

class BackdropHelper(val mContext: Context, val contentView: RoundedRecyclerView, val keyboardHelper: KeyboardHelper) {

    val contentViewHeight = contentView.height
    val contentParams = contentView.layoutParams as RelativeLayout.LayoutParams
    val topHeight = contentView.topHeight

    var isScroll = false
    val animator: ValueAnimator = ValueAnimator.ofInt().apply {
        duration = 300
        addUpdateListener {
            mMargin = it.animatedValue as Int
        }
    }
    var mMargin = 0
        set(value) {
            onMarginChange(value)
            field = value
        }
    var onMarginChangeListener: (margin: Int, ratio: Float) -> Unit = { _, _ -> }
    var isKeyboardUp = false

    private val statusBarSize = makeDP(mContext, 24f) // statusBarSize
    private val swipeMinHeight = makeDP(mContext, 12f) // 짧고 빠른 스와이프 기준 //TODO:// 이거 값 확인해서 바꾸기
    private val maxMarginSize = contentViewHeight - topHeight - contentView.paddingBottom

    init {
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

    private fun onMarginChange(margin: Int) {
        val ratio = margin.toFloat() / maxMarginSize
        val horizontalMargin = ratio * makeDP(mContext, 16f)

        contentParams.setMargins(horizontalMargin.toInt(), margin, horizontalMargin.toInt(), 0)
        contentView.run {
            layoutParams = contentParams
            childAlpha = 1 - ratio
            backColor = ratioARGB(Color.WHITE, 1 - ratio)
            innerColor = ratioARGB(Color.parseColor("#4d442d26"), ratio)
            radius = makeDP(mContext, 16f) - ratio * makeDP(mContext, 4f)
            descendantFocusability =
                    if (ratio == 0f) ViewGroup.FOCUS_AFTER_DESCENDANTS
                    else ViewGroup.FOCUS_BLOCK_DESCENDANTS
            requestFocus()
        }
        onMarginChangeListener.invoke(margin, ratio)
    }

    var degreeY = 0 // 차이
    var lastY = 0 // 바뀌기 전Y
    val onTouch: (View, MotionEvent) -> Boolean =
            onTouch@{ _, e ->
                if (e.action == MotionEvent.ACTION_DOWN) {
                    if (keyboardHelper.isKeyboardVisible()) {
                        keyboardHelper.hideKeyboard()
                        return@onTouch true
                    }
                    isScroll =
                            if (contentView.height == contentViewHeight)
                                e.y <= topHeight * 1.5
                            else
                                contentView.height <= (maxMarginSize).toInt()
                }

                if (!isScroll)
                    return@onTouch false
                val currentTopMargin: Int = Math.max((e.rawY - statusBarSize).toInt(), 0)
                when (e.action) {
                    MotionEvent.ACTION_UP -> {
                        isScroll = false
                        if (currentTopMargin < contentViewHeight / 2)
                            up()
                        else {
                            down()
                        }
                        if (degreeY > swipeMinHeight) {
                            down()
                        }
                        if (degreeY < -swipeMinHeight) {
                            up()
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        mMargin = when {
                            currentTopMargin < 0 -> 0
                            currentTopMargin > maxMarginSize -> maxMarginSize.toInt()
                            else -> currentTopMargin
                        }
                    }
                }
                degreeY = currentTopMargin - lastY
                lastY = currentTopMargin
                true
            }

    fun down() {
        animator.setIntValues(mMargin, maxMarginSize.toInt())
        animator.start()
    }

    fun up() {
        animator.setIntValues(mMargin, 0)
        animator.start()
    }

    companion object {
        fun makeDP(context: Context, dp: Float): Float = context.displayMetrics.density * dp

        fun ratioARGB(color: Int, ratio: Float): Int {
            val a = Color.alpha(color) * ratio
            val r = Color.red(color) * ratio
            val g = Color.green(color) * ratio
            val b = Color.blue(color) * ratio
            return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
        }
    }
}