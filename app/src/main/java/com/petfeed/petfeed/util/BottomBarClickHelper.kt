package com.petfeed.petfeed.util

import android.view.MotionEvent
import android.view.View

class BottomBarClickHelper(val items: Array<View>) {

    private val positions = ArrayList<Pair<Int, Int>>()
    private var currentItem = -1
    private var pressStartTime: Long = 0
    var onClickItem: (View, Int) -> Unit = { v, i -> }

    init {
        items.forEach {
            val pos = IntArray(2)
            it.getLocationInWindow(pos)
            positions.add(Pair(pos[0], pos[1]))
        }
    }

    val onTouch: (MotionEvent) -> Boolean =
            onTouch@{ e ->
                val x = e.rawX
                val y = e.rawY
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        items.forEachIndexed { i, view ->
                            if (checkIsIn(x, y, i)) {
                                currentItem = i
                                pressStartTime = System.currentTimeMillis()
                                return@onTouch true
                            }
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (currentItem == -1)
                            return@onTouch false
                        if (!checkIsIn(x, y, currentItem)) {
                            currentItem = -1
                            return@onTouch true
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        val duration = pressStartTime - System.currentTimeMillis()
                        if (currentItem != -1 && duration < 200) {
                            onClickItem(items[currentItem], currentItem)
                        }
                        currentItem = -1
                    }
                }
                false
            }

    private fun checkIsIn(x: Float, y: Float, pos: Int) = x in positions[pos].first..items[pos].measuredWidth + positions[pos].first
            && y in positions[pos].second..items[pos].measuredWidth + positions[pos].second
}