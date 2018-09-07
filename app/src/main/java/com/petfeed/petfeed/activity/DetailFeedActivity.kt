package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.R
import kotlinx.android.synthetic.main.activity_detail_feed.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class DetailFeedActivity : AppCompatActivity() {

    val comments = ArrayList<Any>().apply {
        add("asdfd")
        add(11)
        add(11)
        add(11)
        add(11)
        add("asdfd")
        add(11)
        add(11)
        add("asdfd")
        add(11)
        add(11)
        add(11)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_feed)

        setRecyclerView()
        backButton.onClick {
            finish()
        }
    }

    fun setRecyclerView() {
        commentRecyclerView.run {
            LastAdapter(comments, BR.item)
                    .map<String>(R.layout.item_comment)
                    .map<Int>(R.layout.item_comment_comment)
                    .into(this)
            layoutManager = LinearLayoutManager(this@DetailFeedActivity)
        }
    }
}
