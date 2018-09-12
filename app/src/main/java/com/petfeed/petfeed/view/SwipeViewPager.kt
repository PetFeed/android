package com.petfeed.petfeed.view

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class SwipeViewPager : ViewPager {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    var isSwipeEnabled: Boolean = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean =
            !isSwipeEnabled && ev!!.action == MotionEvent.ACTION_DOWN && ev.edgeFlags == MotionEvent.EDGE_LEFT

    override fun onTouchEvent(ev: MotionEvent?): Boolean = !isSwipeEnabled


}