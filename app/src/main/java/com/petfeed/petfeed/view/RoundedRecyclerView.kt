package com.petfeed.petfeed.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.petfeed.petfeed.R
import org.jetbrains.anko.childrenSequence

class RoundedRecyclerView : RecyclerView {
    private val outerPaint = Paint().apply {
        isAntiAlias = true
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }
    private val innerPaint = Paint().apply {
        isAntiAlias = true
        color = Color.TRANSPARENT
    }
    var radius = 0f
        set(value) {
            invalidate()
            field = value
        }
    var topHeight = 0f
    var backColor: Int
        get() = backgroundPaint.color
        set(value) {
            backgroundPaint.color = value
            invalidate()
        }
    var innerColor: Int
        get() = innerPaint.color
        set(value) {
            innerPaint.color = value
            invalidate()
        }
    var childAlpha = 1f

    var onInterceptTouchEvent: ((MotionEvent) -> Boolean)? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        getAttrs(attrs!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        getAttrs(attrs!!, defStyleAttr)
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typeArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedRecyclerView, defStyleAttr, 0)
        setTypeArray(typeArray)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedRecyclerView)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        radius = typedArray.getDimension(R.styleable.RoundedRecyclerView_top_radius, height.toFloat())
        topHeight = typedArray.getDimension(R.styleable.RoundedRecyclerView_top_height, 0f)
        backColor = typedArray.getColor(R.styleable.RoundedRecyclerView_back_color, Color.WHITE)
        innerColor = typedArray.getColor(R.styleable.RoundedRecyclerView_inner_color, Color.TRANSPARENT)
        childAlpha = typedArray.getFloat(R.styleable.RoundedRecyclerView_child_alpha, 1f)
        typedArray.recycle()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        drawInners(canvas, backgroundPaint)
        super.dispatchDraw(canvas)
        childrenSequence().forEach {
            it.alpha = childAlpha
        }
        drawInners(canvas, innerPaint)
        drawOuters(canvas)
    }

    private fun drawOuters(canvas: Canvas?) {
        val leftRect = RectF(0f, 0f, radius, radius)
        val rightRect = RectF((width - radius), 0f, width.toFloat(), radius)
        val path = Path().apply {
            addArc(leftRect, 180f, 90f)
            lineTo(0f, 0f)
            lineTo(0f, radius)
            addArc(rightRect, 270f, 90f)
            lineTo(width.toFloat(), 0f)
            lineTo(width.toFloat() - radius, 0f)
        }
        canvas?.drawPath(path, outerPaint)
    }

    private fun drawInners(canvas: Canvas?, paint: Paint) {
        val leftRect = RectF(0f, 0f, radius, radius)
        val rightRect = RectF((width - radius), 0f, width.toFloat(), radius)
        val path = Path().apply {
            addArc(leftRect, 180f, 90f)
            lineTo(width / 2f, 0f)
            lineTo(width / 2f, height.toFloat())
            lineTo(0f, height.toFloat())
            lineTo(0f, radius)
            addArc(rightRect, 270f, 90f)
            lineTo(width.toFloat(), radius)
            lineTo(width.toFloat(), height.toFloat())
            lineTo(width / 2f, height.toFloat())
            lineTo(width / 2f, 0f)
        }
        canvas?.drawPath(path, paint)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if (onInterceptTouchEvent != null) {
            return onInterceptTouchEvent?.invoke(e!!)!! || super.onInterceptTouchEvent(e)
        }
        return super.onInterceptTouchEvent(e)
    }
}