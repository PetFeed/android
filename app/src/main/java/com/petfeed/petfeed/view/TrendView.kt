package com.petfeed.petfeed.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.petfeed.petfeed.R


class TrendView : FrameLayout {
    lateinit var imageView: ImageView
    lateinit var userNameTextView: TextView
    lateinit var tagNameTextView: TextView

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
        getAttrs(attrs!!)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
        getAttrs(attrs!!, defStyleAttr)
    }

    private fun initView() {
        val infService = Context.LAYOUT_INFLATER_SERVICE
        val li = context.getSystemService(infService) as LayoutInflater
        val v = li.inflate(R.layout.layout_trend, this, false)
        v.run {
            addView(this)
            imageView = findViewById(R.id.image)
            userNameTextView = findViewById(R.id.userName)
            tagNameTextView = findViewById(R.id.tagName)

        }
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typeArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TrendView, defStyleAttr, 0)
        setTypeArray(typeArray)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TrendView)
        setTypeArray(typedArray)
    }

    @SuppressLint("SetTextI18n")
    private fun setTypeArray(typedArray: TypedArray) {
        userNameTextView.text = typedArray.getString(R.styleable.TrendView_user)
        tagNameTextView.text = "#${typedArray.getString(R.styleable.TrendView_tag)}"

        val imgId = typedArray.getResourceId(R.styleable.TrendView_img, 0)
        if (imgId != 0)
            imageView.setImageResource(imgId)
        typedArray.recycle()
    }
}