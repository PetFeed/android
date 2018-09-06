package com.petfeed.petfeed.util

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.petfeed.petfeed.util.UIUtils.makeDP
import com.petfeed.petfeed.util.UIUtils.ratioARGB
import com.petfeed.petfeed.view.RoundedRecyclerView

class BackdropHelper(val mContext: Context, val contentView: RoundedRecyclerView, val keyboardHelper: KeyboardHelper) {

    val contentViewHeight = contentView.height
    val contentParams = contentView.layoutParams as RelativeLayout.LayoutParams
    val topHeight = contentView.topHeight

    var isScroll = false

    val animator: CustomAnimator = CustomAnimator().apply {
        duration = 300
        onAnimationUpdate = { mMargin = it }
        onAnimationEnd = { isScroll = false }
        onAnimationCancel = { isScroll = false }
    }

    var mMargin = 0
        set(value) {
            onMarginChange(value)
            field = value
        }
    var onMarginChangeListener: (margin: Int, ratio: Float) -> Unit = { _, _ -> }

    private val statusBarSize = makeDP(mContext, 24f) // statusBarSize
    private val swipeMinHeight = makeDP(mContext, 12f) // 짧고 빠른 스와이프 기준 //TODO:// 이거 값 확인해서 바꾸기
    private val maxMarginSize = contentViewHeight - topHeight - contentView.paddingBottom

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
        animator.run {
            startValue = mMargin
            endValue = maxMarginSize.toInt()
            start()
        }
    }

    fun up() {
        animator.run {
            startValue = mMargin
            endValue = 0
            start()
        }
    }
}