package com.petfeed.petfeed.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager


class KeyboardHelper(val activity: Activity) {

    private val KEYBOARD_CHECK_DP = 100
    private val activityRoot = getActivityRoot(activity)
    private val visibleThreshold = Math.round(KEYBOARD_CHECK_DP * activity.resources.displayMetrics.density)

    fun isKeyboardVisible(): Boolean {
        val r = Rect()
        activityRoot.getWindowVisibleDisplayFrame(r)
        val heightDiff = activityRoot.rootView.height - r.height()

        return heightDiff > visibleThreshold
    }

    fun hideKeyboard() {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)

    }

    private fun getActivityRoot(activity: Activity): View {
        return (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
    }
}