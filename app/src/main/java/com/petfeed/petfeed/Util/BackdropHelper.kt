package com.petfeed.petfeed.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.petfeed.petfeed.view.RoundedRecyclerView
import org.jetbrains.anko.displayMetrics

class BackdropHelper(val mContext: Context, val contentView: RoundedRecyclerView) {

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
        val ratio = margin.toFloat() / (contentViewHeight - topHeight - contentView.paddingBottom)
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

    val onTouch: (View, MotionEvent) -> Boolean =
            onTouch@{ _, e ->
                if (e.action == MotionEvent.ACTION_DOWN) {
                    isScroll =
                            if (contentView.height == contentViewHeight)
                                e.y <= topHeight * 1.5
                            else
                                contentView.height <= (contentViewHeight - topHeight - contentView.paddingBottom).toInt()
                }

                if (!isScroll)
                    return@onTouch false

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
                            currentTopMargin > (contentViewHeight - topHeight - contentView.paddingBottom) -> (contentViewHeight - topHeight - contentView.paddingBottom).toInt()
                            else -> currentTopMargin
                        }
                    }
                }
                true
            }

    fun down() {
        animator.setIntValues(mMargin, (contentViewHeight - topHeight - contentView.paddingBottom).toInt())
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