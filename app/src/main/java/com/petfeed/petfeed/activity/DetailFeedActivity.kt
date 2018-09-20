package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.LuvDonateDialog
import com.petfeed.petfeed.R
import com.petfeed.petfeed.model.Board
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.DataHelper
import com.petfeed.petfeed.util.network.NetworkHelper
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

    var board: Board? = null
    lateinit var requestManager: RequestManager
    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white1, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_feed)

        requestManager = Glide.with(this)
        feedGridView.requestManager = requestManager
        setRecyclerView()
        backButton.onClick {
            finish()
        }
        luvButton.onClick {
            LuvDonateDialog(this@DetailFeedActivity).show()
        }

        board = DataHelper.datas?.selectedBoard

        feedGridView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                feedGridView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (board != null) {
                    feedGridView.imageUrls.addAll(board!!.pictures)
                    feedGridView.viewUpdate()
                    contents.text = board!!.contents
                    requestManager
                            .load(NetworkHelper.url + board!!.writer.profile)
                            .into(writerProfileImage)
                    writerName.text = board!!.writer.nickname
                }
                topContainer.scrollTo(0, 0)
            }
        })


        requestManager
                .load(NetworkHelper.url + DataHelper.datas!!.user.profile)
                .into(profileImage)
    }

    private fun setRecyclerView() {
        commentRecyclerView.run {
            LastAdapter(comments, BR.item)
                    .map<String>(R.layout.item_comment)
                    .map<Int>(R.layout.item_comment_comment)
                    .into(this)
            layoutManager = LinearLayoutManager(this@DetailFeedActivity)
        }
    }
}
