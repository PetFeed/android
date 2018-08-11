package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.MyPagerAdapter
import com.petfeed.petfeed.R
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.BackdropHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.backgroundColor
import java.util.*

class MainActivity : AppCompatActivity() {

    var boards = ArrayList<Any>().apply {
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
    }
    lateinit var backdropHelper: BackdropHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.brown1, true)
        setContentView(R.layout.activity_main)


        LastAdapter(boards, BR.item)
                .map<String>(R.layout.item_board)
                .into(board_recycler_view)

        board_recycler_view.layoutManager = LinearLayoutManager(this)



        board_recycler_view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                board_recycler_view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                setBackDropHelper()
            }
        })
        view_pager.adapter = MyPagerAdapter(supportFragmentManager)
    }

    fun setBackDropHelper() {
        backdropHelper = BackdropHelper(this@MainActivity, top_view, board_recycler_view)
        val brown = ContextCompat.getColor(this, R.color.brown1)
        backdropHelper.onMarginChangeListener = { _, ratio ->
            val color = BackdropHelper.ratioARGB(brown, 1 - ratio)
            dummy_view.alpha = 0.3f * (1 - ratio)
            ActivityUtils.statusBarSetting(window,
                    this,
                    iconWhite = ratio < 0.5f,
                    color = color)
            view_pager.alpha = ratio
            content_container.backgroundColor = color
            board_recycler_view.run {
                descendantFocusability =
                        if (ratio == 0f) ViewGroup.FOCUS_AFTER_DESCENDANTS
                        else ViewGroup.FOCUS_BLOCK_DESCENDANTS
                requestFocus()
            }
        }
    }
}
