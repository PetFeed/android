package com.petfeed.petfeed.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.petfeed.petfeed.R

class TrendRankView : LinearLayout {
    lateinit var titleTextView: TextView
    lateinit var countTextView: TextView
    lateinit var rankTextView: TextView

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

    fun initView() {
        val infService = Context.LAYOUT_INFLATER_SERVICE
        val li = context.getSystemService(infService) as LayoutInflater
        val v = li.inflate(R.layout.layout_trend_rank, this, false)
        addView(v)
        v.run {
            titleTextView = findViewById(R.id.title)
            countTextView = findViewById(R.id.count)
            rankTextView = findViewById(R.id.rank)
        }
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typeArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TrendRankView, defStyleAttr, 0)
        setTypeArray(typeArray)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.TrendRankView)
        setTypeArray(typedArray)
    }

    @SuppressLint("SetTextI18n")
    private fun setTypeArray(typedArray: TypedArray) {
        countTextView.text = "새 게시물 ${typedArray.getString(R.styleable.TrendRankView_count)}개"
        titleTextView.text = "#${typedArray.getString(R.styleable.TrendRankView_title)}"
        rankTextView.text = typedArray.getString(R.styleable.TrendRankView_rank)
        typedArray.recycle()
    }
}