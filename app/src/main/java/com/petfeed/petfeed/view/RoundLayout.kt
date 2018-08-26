package com.petfeed.petfeed.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.petfeed.petfeed.R

class RoundLayout : FrameLayout {
    private val outerPaint = Paint().apply {
        isAntiAlias = true
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    //    private val innerPaint = Paint().apply {
//        isAntiAlias = true
//        color = Color.TRANSPARENT
//    }
    var mTopLeftRadius = 0f
    var mTopRightRadius = 0f
    var mBottomLeftRadius = 0f
    var mBottomRightRadius = 0f
    var outerColor = 0
        set(value) {
            outerPaint.color = value
            field = value
        }

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        getAttrs(attrs!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {
        getAttrs(attrs!!, defStyleAttr)
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typeArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundLayout, defStyleAttr, 0)
        setTypeArray(typeArray)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundLayout)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val radius = typedArray.getDimension(R.styleable.RoundLayout_radius, 0f)
        mTopLeftRadius = radius
        mTopRightRadius = radius
        mBottomLeftRadius = radius
        mBottomRightRadius = radius

        mTopRightRadius = typedArray.getDimension(R.styleable.RoundLayout_topRightRadius, radius)
        mTopLeftRadius = typedArray.getDimension(R.styleable.RoundLayout_topLeftRadius, radius)
        mBottomRightRadius = typedArray.getDimension(R.styleable.RoundLayout_bottomRightRadius, radius)
        mBottomLeftRadius = typedArray.getDimension(R.styleable.RoundLayout_bottomLeftRadius, radius)
        outerColor = typedArray.getColor(R.styleable.RoundLayout_outerColor, Color.TRANSPARENT)

        typedArray.recycle()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        super.dispatchDraw(canvas)
        drawOuters(canvas)
    }

    private fun drawOuters(canvas: Canvas?) {
        val mBottomRightRadius = min(this@RoundLayout.mBottomRightRadius, height.toFloat(), width.toFloat())
        val mBottomLeftRadius = min(this@RoundLayout.mBottomLeftRadius, height.toFloat(), width.toFloat())
        val mTopRightRadius = min(this@RoundLayout.mTopRightRadius, height.toFloat(), width.toFloat())
        val mTopLeftRadius = min(this@RoundLayout.mTopLeftRadius, height.toFloat(), width.toFloat())
        val bottomRightRect = RectF(width - mBottomRightRadius, height - mBottomRightRadius, width.toFloat(), height.toFloat())
        val bottomLeftRect = RectF(0f, height - mBottomLeftRadius, mBottomLeftRadius, height.toFloat())
        val topLeftRect = RectF(0f, 0f, mTopLeftRadius, mTopLeftRadius)
        val topRightRect = RectF(width - mTopRightRadius, 0f, width.toFloat(), mTopRightRadius)
        val path = Path().apply {
            // TopLeft
            addArc(topLeftRect, 180f, 90f)
            lineTo(0f, 0f)
            lineTo(0f, mTopLeftRadius)
            // TopRight
            addArc(topRightRect, 270f, 90f)
            lineTo(width.toFloat(), 0f)
            lineTo(width - mTopRightRadius, 0f)
            // BottomLeft
            addArc(bottomLeftRect, 90f, 90f)
            lineTo(0f, height.toFloat())
            lineTo(mBottomLeftRadius, height.toFloat())
            // BottomRight
            addArc(bottomRightRect, 0f, 90f)
            lineTo(width.toFloat(), height.toFloat())
            lineTo(width.toFloat(), height - mBottomRightRadius)
        }
        outerPaint.xfermode =
                PorterDuffXfermode(if (outerPaint.color != Color.TRANSPARENT) PorterDuff.Mode.OVERLAY else PorterDuff.Mode.CLEAR)
        canvas?.drawPath(path, outerPaint)
    }

//    private fun drawInners(canvas: Canvas?, paint: Paint) {
//        val bottomRightRect = RectF(width - mBottomRightRadius, height - mBottomRightRadius, width.toFloat(), height.toFloat())
//        val bottomLeftRect = RectF(0f, height - mBottomLeftRadius, mBottomLeftRadius, height.toFloat())
//        val topLeftRect = RectF(0f, 0f, mTopLeftRadius, mTopLeftRadius)
//        val topRightRect = RectF(width - mTopRightRadius, 0f, width.toFloat(), mTopRightRadius)
//        val path = Path().apply {
//            // topLeft
//            addArc(topLeftRect, 180f, 90f)
//            lineTo(width / 2f, 0f)
//            lineTo(width / 2f, height / 2f)
//            lineTo(0f, height / 2f)
//            lineTo(0f, mTopLeftRadius)
//            // topRight
//            addArc(topRightRect, 270f, 90f)
//            lineTo(width.toFloat(), height / 2f)
//            lineTo(width / 2f, height / 2f)
//            lineTo(width / 2f, 0f)
//            lineTo(width - mTopRightRadius, 0f)
//            // bottomLeft
//            addArc(bottomLeftRect, 90f, 90f)
//            lineTo(0f, height / 2f)
//            lineTo(width / 2f, height / 2f)
//            lineTo(width / 2f, height.toFloat())
//            lineTo(mBottomLeftRadius, height.toFloat())
//            // bottomRight
//            addArc(bottomRightRect, 0f, 90f)
//            lineTo(width / 2f, height.toFloat())
//            lineTo(width / 2f, height / 2f)
//            lineTo(width.toFloat(), height / 2f)
//            lineTo(width.toFloat(), height - mBottomRightRadius)
//        }
//        canvas?.drawPath(path, paint)
//    }

    override fun performClick(): Boolean = super.performClick()


    private fun min(n1: Float, n2: Float, n3: Float): Float = Math.min(Math.min(n1, n2), n3)
}