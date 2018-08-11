package com.petfeed.petfeed.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.petfeed.petfeed.R

class BackdropTopView : View {
    var innerColor = Color.WHITE
        set(value) {
            innerPaint.color = value
            field = value
            requestLayout()
        }
    var outerColor = Color.WHITE
        set(value) {
            outerPaint.color = value
            field = value
            requestLayout()
        }
    var radius = 0f

    private var outerPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }
    private var innerPaint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val outerBackgroundPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        getAttrs(attrs!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        getAttrs(attrs!!, defStyleAttr)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        drawOuterBackground(canvas)
        drawOuters(canvas)
        drawInners(canvas)
    }

    private fun drawOuterBackground(canvas: Canvas?) {
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
        canvas?.drawPath(path, outerBackgroundPaint)
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

    private fun drawInners(canvas: Canvas?) {
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
        canvas?.drawPath(path, innerPaint)
    }

    var isInnerSolid = false
        set(value) {
            field = value
            if (field) {
                innerPaint = Paint().apply {
                    isAntiAlias = true
                    color = innerColor
                }
            } else {
                innerPaint = Paint().apply {
                    isAntiAlias = true
                    xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }
            }
            requestLayout()
        }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typeArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.BackdropTopView, defStyleAttr, 0)
        setTypeArray(typeArray)

    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.BackdropTopView)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        innerColor = typedArray.getColor(R.styleable.BackdropTopView_innerColor, Color.TRANSPARENT)
        outerColor = typedArray.getColor(R.styleable.BackdropTopView_outerColor, Color.BLACK)
        isInnerSolid = typedArray.getBoolean(R.styleable.BackdropTopView_isInnerSolid, false)
        radius = typedArray.getDimension(R.styleable.BackdropTopView_radius, height.toFloat())
        typedArray.recycle()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    companion object {
        val COLER = 0
        val NOCOLER = 1
    }
}