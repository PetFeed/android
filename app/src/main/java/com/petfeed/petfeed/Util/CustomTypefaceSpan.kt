package com.petfeed.petfeed.util

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

class CustomTypefaceSpan(val tf: Typeface) : TypefaceSpan("") {

    override fun updateDrawState(paint: TextPaint?) {
        super.updateDrawState(paint)
        paint?.typeface = tf
    }

    override fun updateMeasureState(paint: TextPaint?) {
        super.updateMeasureState(paint)
        paint?.typeface = tf
    }
}