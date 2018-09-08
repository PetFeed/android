package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.R
import com.petfeed.petfeed.adapter.MyPagerAdapter
import com.petfeed.petfeed.databinding.ItemBoardBinding
import com.petfeed.petfeed.util.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onPageChangeListener

class MainActivity : AppCompatActivity() {

    var boards = ArrayList<Any>().apply {
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
    }
    val bottomItems = ArrayList<View>() // view
    lateinit var backdropHelper: BackdropHelper
    lateinit var bottomBarClickHelper: BottomBarClickHelper
    lateinit var keyboardHelper: KeyboardHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.brown1, true)
        setContentView(R.layout.activity_main)

        setViewPager()
        viewPager.requestFocus()
        boardRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                boardRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                setRecyclerView()
            }
        })
    }

    fun setViewPager() {
        bottomItems.run {
            add(bottomTabItem1)
            add(bottomTabItem2)
            add(bottomTabItem3)
            add(bottomTabItem4)
            add(bottomTabItem5)
        }
        viewPager.adapter = MyPagerAdapter(supportFragmentManager)
        viewPager.onPageChangeListener {
            onPageSelected {
                bottomItems.filterIndexed { i, _ -> i != 2 }.forEachIndexed { i, v ->
                    v.alpha = if (i == it) 1.0f else 0.3f
                }
            }
        }
        viewPager.currentItem = 0
    }

    fun setRecyclerView() {
        val statusBarSize = UIUtils.makeDP(this, 24f)
        boardRecyclerView.run {
            layoutManager = LinearLayoutManager(this@MainActivity)
            LastAdapter(boards, BR.item)
                    .map<String, ItemBoardBinding>(R.layout.item_board) {

                        // 아이탬 클릭
                        var pressStartTime: Long = 0
                        var isValid = false

                        onCreate {
                            it.itemView.setOnTouchListener itemClick@{ v, e ->
                                val y = e.rawY
                                when (e.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        if (y < bottomTabBar.y && y > (topHeight * 3 + statusBarSize)) {
                                            isValid = true
                                            pressStartTime = System.currentTimeMillis()
                                        }
                                    }
                                    MotionEvent.ACTION_MOVE -> {
                                        if (isValid) {
                                            if (y >= bottomTabBar.y || y <= topHeight * 3 + statusBarSize) {
                                                isValid = false
                                            }
                                        }
                                    }
                                    MotionEvent.ACTION_UP -> {
                                        val duration = pressStartTime - System.currentTimeMillis()
                                        if (duration < 200) {
                                            startActivity<DetailFeedActivity>()
                                        }

                                    }
                                }
                                isValid
                            }
                        }
                    }
                    .into(this)
            setBackDropHelper()
            setBottomBarClickHelper()
            this@run.setOnTouchListener { v, event ->
                backdropHelper.onTouch(v, event) or bottomBarClickHelper.onTouch(v, event)
            }
        }
    }

    fun setBottomBarClickHelper() { // bottomBarItem onClicks
        bottomBarClickHelper = BottomBarClickHelper(bottomItems.toTypedArray()).apply {
            onClickItem = { v, i ->
                when (i) {
                    in 0..1 -> {
                        viewPager.currentItem = i
                        backdropHelper.down()
                    }
                    2 -> {
                        startActivity<WriteActivity>()
                        overridePendingTransition(R.anim.slide_in_up, android.R.anim.fade_out)
                    }
                    else -> {
                        viewPager.currentItem = i - 1
                        backdropHelper.down()
                    }
                }
            }
        }
    }

    fun setBackDropHelper() {
        keyboardHelper = KeyboardHelper(this@MainActivity)
        backdropHelper = BackdropHelper(this@MainActivity, boardRecyclerView, keyboardHelper)
        val brown = ContextCompat.getColor(this, R.color.brown1)
        val white = ContextCompat.getColor(this, R.color.white2)

        backdropHelper.onMarginChangeListener = { _, ratio ->
            dummyView.alpha = 0.3f * (1 - ratio)

            ActivityUtils.statusBarSetting(window,
                    this,
                    iconWhite = ratio < 0.5f,
                    color = ColorUtils.blendARGB(brown, white, ratio))

            viewPager.alpha = ratio
            contentContainer.backgroundColor = UIUtils.ratioARGB(brown, 1 - ratio)
        }
    }


}
