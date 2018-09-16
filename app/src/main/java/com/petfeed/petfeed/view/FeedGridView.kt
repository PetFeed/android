package com.petfeed.petfeed.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.petfeed.petfeed.R
import com.petfeed.petfeed.view.FeedGridView.LayoutMode.*

class FeedGridView : ConstraintLayout {
    var imageUrls = ArrayList<String>()
    var images = Array<Bitmap?>(5) { null }
    var imageViews = ArrayList<ImageView>()
    var distance = 0f
    var fontSize = 0f
    private var layoutMode: LayoutMode = SQUARE2

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        getAttrs(attrs!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        getAttrs(attrs!!, defStyleAttr)
    }

    private fun initImages() {
        imageUrls.forEachIndexed { index, s ->
            if (index > 3)
                return@forEachIndexed
            Glide.with(context).asBitmap().load(s)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            images[index] = resource
                            val max = if (imageUrls.size >= 4) 4 else imageUrls.size
                            if (images.filter { it != null }.size == max) {
                                initView()
                            }
                        }
                    })

        }
    }

    fun viewUpdate() {
        removeAllViews()
        images.forEachIndexed { i, _ -> images[i] = null }
        imageViews.clear()
        initImages()
    }

    private fun initView() {
        val ratio = images[0]!!.ratio()
        val shape = when {
            imageUrls.size == 1 -> 0
            ratio > 1.2f -> 2
            ratio < 0.8f -> 3
            else -> 1
        }
        val count = if (imageUrls.size != 1) images.filter { it != null }.size else 1
        layoutMode = LayoutMode.get(shape, count)
        when (layoutMode) {
            ONE -> setViewOne()
            SQUARE2 -> setViewSquare2()
            SQUARE3 -> setViewSquare3()
            SQUARE4 -> setViewSquare4()
            HORIZONTAL2 -> setViewHorizontal2()
            HORIZONTAL3 -> setViewHorizontal3()
            HORIZONTAL4 -> setViewHorizontal4()
            VERTICAL2 -> setViewVertical2()
            VERTICAL3 -> setViewVertical3()
            VERTICAL4 -> setViewVertical4()
        }
        addImageViews()
        lastOverlay()
    }


    private fun addImageViews() {
        imageViews.forEachIndexed { i, it ->
            it.setBackgroundColor(Color.TRANSPARENT)
            removeView(it)
            addView(it)
            Glide.with(context)
                    .load(imageUrls[i])
                    .into(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun lastOverlay() {
        if (imageUrls.size <= imageViews.size)
            return
        val tv = TextView(context).apply {
            val lastId = imageViews.last().id
            this.layoutParams = ConstraintLayout.LayoutParams(0, 0).apply {
                startToStart = lastId
                endToEnd = lastId
                topToTop = lastId
                bottomToBottom = lastId
            }
            this.setBackgroundColor(Color.parseColor("#99000000"))
            this.text = "+ ${(imageUrls.size - imageViews.size)}장"
            this.typeface = ResourcesCompat.getFont(context, R.font.nanum_square_round_extra_bold)
            this.setTextColor(Color.WHITE)
            this.textAlignment = View.TEXT_ALIGNMENT_CENTER
            this.gravity = CENTER
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
        }
        addView(tv)
    }

    private fun setViewOne() {
        val ratio = images[0]!!.ratio()
        images.filter { it != null }.forEach {
            makeImageView(width, (width * ratio).toInt())
        }
        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }
    }

    private fun setViewSquare2() {
        images.filter { it != null }.forEach {
            makeImageView(((width - distance) / 2f).toInt(), ((width - distance) / 2f).toInt())
        }
        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                when (i) {
                    0 -> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    1 -> {
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
            }
        }
    }

    private fun setViewSquare3() {
        images.filter { it != null }.forEach {
            makeImageView(((width - distance * 2) / 3f).toInt(), ((width - distance * 2) / 3f).toInt())
        }
        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                when (i) {
                    0 -> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    }

                    1 -> {
                        startToEnd = imageViews[0].id
                        endToStart = imageViews[2].id
                    }

                    2 -> {
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
            }
        }
    }

    private fun setViewSquare4() {
        images.filter { it != null }.forEach {
            makeImageView(((width - distance) / 2f).toInt(), ((width - distance) / 2f).toInt())
        }
        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                when (i) {
                    0 -> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    1 -> {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    2 -> {
                        topMargin = distance.toInt()
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        topToBottom = imageViews[0].id
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    3 -> {
                        topMargin = distance.toInt()
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topToBottom = imageViews[0].id
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
            }
        }
    }

    private fun setViewHorizontal2() {
        images.filter { it != null }.forEachIndexed { i, _ ->
            makeImageView(((width - distance) / 2f).toInt(), width)
        }

        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                when (i) {
                    0 -> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID

                    }
                    1 -> {
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
            }
        }
    }

    private fun setViewHorizontal3() {
        images.filter { it != null }.forEachIndexed { i, _ ->
            if (i == 0)
                makeImageView(((width - distance) / 2f).toInt(), width)
            else
                makeImageView(((width - distance) / 2f).toInt(), ((width - distance) / 2f).toInt())

        }

        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                when (i) {
                    0 -> {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID

                    }
                    1 -> {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    2 -> {
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
                if (i != 0) {
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
        }
    }

    private fun setViewHorizontal4() {
        images.filter { it != null }.forEachIndexed { i, _ ->
            if (i == 0)
                makeImageView(((width - distance * 2) / 3f * 2f + distance).toInt(), width)
            else
                makeImageView(((width - distance * 2) / 3f).toInt(), ((width - distance * 2) / 3f).toInt())
        }

        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                when (i) {
                    0 -> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    1 -> {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    2 -> {
                        topToBottom = imageViews[i + 1].id
                        bottomToTop = imageViews[i - 1].id
                    }
                    3 -> {
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
                if (i != 0) {
                    endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
        }
    }

    private fun setViewVertical2() {
        images.filter { it != null }.forEachIndexed { i, _ ->
            makeImageView(width, ((width - distance) / 2f).toInt())
        }

        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                when (i) {
                    0 -> {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToTop = imageViews[1].id
                    }
                    1 -> {
                        topMargin = distance.toInt()
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        topToBottom = imageViews[0].id
                    }
                }
            }
        }
    }

    private fun setViewVertical3() {
        images.filter { it != null }.forEachIndexed { i, _ ->
            if (i == 0)
                makeImageView(width, ((width - distance) / 2f).toInt())
            else
                makeImageView(((width - distance) / 2f).toInt(), ((width - distance) / 2f).toInt())
        }

        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {

                when (i) {
                    0 -> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToTop = imageViews[1].id
                    }
                    1 -> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    2 -> {
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
                if (i != 0) {
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    topMargin = distance.toInt()
                    topToBottom = imageViews[0].id
                }
            }
        }
    }

    private fun setViewVertical4() {
        images.filter { it != null }.forEachIndexed { i, _ ->
            if (i == 0)
                makeImageView(width, ((width - distance * 2) / 3f * 2f + distance).toInt())
            else
                makeImageView(((width - distance * 2) / 3f).toInt(), ((width - distance * 2) / 3f).toInt())
        }

        imageViews.forEachIndexed { i, it ->
            it.layoutParams = (it.layoutParams as ConstraintLayout.LayoutParams).apply {
                when (i) {
                    0 -> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToTop = imageViews[1].id
                    }
                    1 -> {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                    2 -> {
                        startToEnd = imageViews[i + 1].id
                        endToStart = imageViews[i - 1].id
                    }
                    3 -> {
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
                if (i != 0) {
                    topMargin = distance.toInt()
                    topToBottom = imageViews[0].id
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
        }
    }

    private fun makeImageView(w: Int, h: Int) {
        imageViews.add(ImageView(context).apply {
            this.layoutParams = ConstraintLayout.LayoutParams(w, h)
            scaleType = ImageView.ScaleType.CENTER_CROP
            this.id = imageViews.size + 1
        })
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typeArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.FeedGridView, defStyleAttr, 0)
        setTypeArray(typeArray)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.FeedGridView)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        distance = typedArray.getDimension(R.styleable.FeedGridView_distance, 0f)
        fontSize = typedArray.getDimension(R.styleable.FeedGridView_fontSize, 0f)
        typedArray.recycle()
    }

    private fun Bitmap.ratio(): Float = this.height.toFloat() / this.width.toFloat()


    private enum class LayoutMode(val shape: Int = 0, val count: Int = 0) {

        ONE(0, 1),

        SQUARE2(1, 2), SQUARE3(1, 3), SQUARE4(1, 4),

        HORIZONTAL2(2, 2), HORIZONTAL3(2, 3), HORIZONTAL4(2, 4),

        VERTICAL2(3, 2), VERTICAL3(3, 3), VERTICAL4(3, 4);

        companion object {
            fun get(shape: Int = 0, count: Int = 0): LayoutMode {
                return values().first { it.shape == shape && it.count == count }
            }

        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewUpdate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        imageViews.clear()
    }
}