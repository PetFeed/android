package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewTreeObserver
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.R
import com.petfeed.petfeed.util.BackdropHelper
import kotlinx.android.synthetic.main.activity_main.*
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
        setContentView(R.layout.activity_main)


        LastAdapter(boards, BR.item)
                .map<String>(R.layout.item_board)
                .into(board_recycler_view)

        board_recycler_view.layoutManager = LinearLayoutManager(this)



        board_recycler_view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                board_recycler_view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                backdropHelper = BackdropHelper(this@MainActivity, top_view, board_recycler_view, dummy_view)
            }
        })
    }
}
