package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewTreeObserver
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.MyPagerAdapter
import com.petfeed.petfeed.R
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.BackdropHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

class MainActivity : AppCompatActivity() {

    var boards = ArrayList<Any>().apply {
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
        setRecyclerView()
        view_pager.adapter = MyPagerAdapter(supportFragmentManager)
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
                }
            })
        }
    }

    fun setBackDropHelper() {
        backdropHelper = BackdropHelper(this@MainActivity, board_recycler_view)
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
