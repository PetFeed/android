package com.petfeed.petfeed.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.content.res.ResourcesCompat
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.petfeed.petfeed.GlideRequests
import com.petfeed.petfeed.R
import com.petfeed.petfeed.view.FeedGridView.LayoutMode.*
import kotlin.math.min

class FeedGridView : ConstraintLayout {


    var imageUrls = ArrayList<String>()
    var imageViews = ArrayList<ImageView>()
    var distance = 0f
    var fontSize = 0f
    var imageCount = 0

    lateinit var firstImage: Bitmap
    private var layoutMode: LayoutMode = SQUARE2
    var requestManager: GlideRequests? = null
    var mImages: ArrayList<Bitmap> = ArrayList()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        getAttrs(attrs!!)
        viewUpdate()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        getAttrs(attrs!!, defStyleAttr)
        viewUpdate()
    }

    private fun initImages() {
        if (imageUrls.size == 0)
            return
        requestManager!!.asBitmap().load(imageUrls.first()).thumbnail(0.1f)
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        firstImage = resource
                        initView()
                    }
                })

    }

    fun viewUpdate() {
        imageViews.clear()
        removeAllViews()
        initImages()
    }

    fun initView() {
        val ratio = firstImage.ratio()
        val shape = when {
            imageUrls.size == 1 -> 0
            ratio > 1.2f -> 2
            ratio < 0.8f -> 3
            else -> 1
        }
        imageCount = min(imageUrls.size, 4)
        try {

            layoutMode = LayoutMode.get(shape, imageCount)
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
            Log.e("FeedGridView", "shape  ${shape} imageCount  ${imageCount}}")
            return
        }
        if (imageViews.size != 0)
            imageViews.clear()
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


    var loadedCount = 0
    private fun addImageViews() {
        loadedCount = 0
        if (requestManager == null)
            return
        imageViews.forEachIndexed { i, it ->
            it.setBackgroundColor(Color.TRANSPARENT)
            removeView(it)
            addView(it)
            loadImage(it, imageUrls[i])
        }
    }

    private fun loadImage(imageView: ImageView, imageUrl: String) {
        requestManager!!
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .signature(ObjectKey(imageUrl))
                .thumbnail(0.1f)
                .centerInside()
                .into(imageView)
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
        val ratio = firstImage.ratio()
        (0 until imageCount).forEach { _ ->
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
        (0 until imageCount).forEach { _ ->
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
        (0 until imageCount).forEach { _ ->
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
        (0 until imageCount).forEach { _ ->
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
        (0 until imageCount).forEachIndexed { _, _ ->
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
        (0 until imageCount).forEachIndexed { i, _ ->
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
        (0 until imageCount).forEachIndexed { i, _ ->
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
        (0 until imageCount).forEachIndexed { i, _ ->
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
                        topMargin = distance.toInt() * 2
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        topToBottom = imageViews[0].id
                    }
                }
            }
        }
    }

    private fun setViewVertical3() {
        (0 until imageCount).forEachIndexed { i, _ ->
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
                    topMargin = distance.toInt() * 2
                    topToBottom = imageViews[0].id
                }
            }
        }
    }

    private fun setViewVertical4() {
        (0 until imageCount).forEachIndexed { i, _ ->
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
                    topMargin = distance.toInt() * 2
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

    fun getImages(): ArrayList<Bitmap> {
        val images = ArrayList<Bitmap>()
        imageViews.forEach {
            images.add((it.drawable as BitmapDrawable).bitmap)
        }
        return images
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
}