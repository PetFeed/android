package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewTreeObserver
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.MyPagerAdapter
import com.petfeed.petfeed.R
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.BackdropHelper
import com.petfeed.petfeed.util.BottomBarClickHelper
import com.petfeed.petfeed.util.KeyboardHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.backgroundColor
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
        setRecyclerView()
    }

    fun setViewPager() {
        bottomItems.run {
            add(bottom_tab_item1)
            add(bottom_tab_item2)
            add(bottom_tab_item3)
            add(bottom_tab_item4)
            add(bottom_tab_item5)
        }
        view_pager.adapter = MyPagerAdapter(supportFragmentManager)
        view_pager.onPageChangeListener {
            onPageSelected {
                bottomItems.filterIndexed { i, _ -> i != 2 }.forEachIndexed { i, v ->
                    v.alpha = if (i == it) 1.0f else 0.3f
                }
            }
        }
        view_pager.currentItem = 0
    }

    fun setRecyclerView() {
        board_recycler_view.run {
            LastAdapter(boards, BR.item)
                    .map<String>(R.layout.item_board)
                    .into(this)
            layoutManager = LinearLayoutManager(this@MainActivity)
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    this@run.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    setBackDropHelper()
                    setBottomBarClickHelper()
                    this@run.setOnTouchListener { v, event ->
                        backdropHelper.onTouch(v, event) or bottomBarClickHelper.onTouch(v, event)
                    }
                }
            })
        }
    }

    fun setBottomBarClickHelper() { // bottomBarItem onClicks
        bottomBarClickHelper = BottomBarClickHelper(bottomItems.toTypedArray()).apply {
            onClickItem = { v, i ->
                when (i) {
                    in 0..1 -> {
                        view_pager.currentItem = i
                        backdropHelper.down()
                    }
                    2 -> Unit //TODO:// 그그그 가운데 글올리는거
                    else -> {
                        view_pager.currentItem = i - 1
                        backdropHelper.down()
                    }
                }
            }
        }
    }

    fun setBackDropHelper() {
        keyboardHelper = KeyboardHelper(this@MainActivity)

        backdropHelper = BackdropHelper(this@MainActivity, board_recycler_view, keyboardHelper)
        val brown = ContextCompat.getColor(this, R.color.brown1)
        val white = ContextCompat.getColor(this, R.color.white2)

        backdropHelper.onMarginChangeListener = { _, ratio ->
            dummy_view.alpha = 0.3f * (1 - ratio)

            ActivityUtils.statusBarSetting(window,
                    this,
                    iconWhite = ratio < 0.5f,
                    color = ColorUtils.blendARGB(brown, white, ratio))

            view_pager.alpha = ratio
            content_container.backgroundColor = BackdropHelper.ratioARGB(brown, 1 - ratio)
        }
    }
}
