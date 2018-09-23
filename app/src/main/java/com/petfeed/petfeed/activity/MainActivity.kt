package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.github.nitrico.lastadapter.LastAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.petfeed.petfeed.*
import com.petfeed.petfeed.adapter.MainPagerAdapter
import com.petfeed.petfeed.databinding.ItemBoardBinding
import com.petfeed.petfeed.model.Board
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.util.*
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onPageChangeListener
import org.jetbrains.anko.support.v4.onRefresh
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var boards = ArrayList<Board>()
    val bottomItems = ArrayList<View>() // view
    lateinit var backdropHelper: BackdropHelper
    lateinit var bottomBarClickHelper: BottomBarClickHelper
    lateinit var keyboardHelper: KeyboardHelper
    lateinit var requestManager: GlideRequests

    val dateFormat = SimpleDateFormat("MMM d일 a KK시 mm분", Locale.KOREAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.statusBarSetting(window, this, R.color.brown1, true)
        setContentView(R.layout.activity_main)

        requestManager = GlideApp.with(this)

        setViewPager()
        viewPager.requestFocus()
        boardRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                boardRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                setRecyclerView()
                async(UI) {
                    getBoards()
                }
            }
        })

        swipeRefreshLayout.onRefresh {
            async(UI) {
                getBoards()
            }
        }
    }

    private fun setViewPager() {
        bottomItems.run {
            add(bottomTabItem1)
            add(bottomTabItem2)
            add(bottomTabItem3)
            add(bottomTabItem4)
            add(bottomTabItem5)
        }
        viewPager.adapter = MainPagerAdapter(supportFragmentManager)
        viewPager.onPageChangeListener {
            onPageSelected {
                bottomItems.filterIndexed { i, _ -> i != 2 }.forEachIndexed { i, v ->
                    v.alpha = if (i == it) 1.0f else 0.3f
                }
            }
        }
        viewPager.currentItem = 0
    }

    private suspend fun getBoards() {
        async(CommonPool) { NetworkHelper.retrofitInstance.getAllBoards(DataHelper.datas!!.token).execute() }.await().apply {

            if (!isSuccessful) {
                swipeRefreshLayout.isRefreshing = false
                return@apply
            }

            val json: JSONObject = JSONObject(body()!!.string())
            val isSuccess = json.getBoolean("success")

            if (!isSuccess) {
                swipeRefreshLayout.isRefreshing = false
                return@apply
            }
            DataHelper.datas?.mainBoards = Gson().fromJson(json.getString("data"), object : TypeToken<ArrayList<Board>>() {}.type)

            DataHelper.datas?.mainBoards?.forEach {
                it.pictures.forEachIndexed { index, s ->
                    it.pictures[index] = NetworkHelper.url + s
                }
            }

            boards.run {
                clear()
                addAll(DataHelper.datas!!.mainBoards)
            }

            boardRecyclerView.adapter!!.notifyDataSetChanged()
        }
        swipeRefreshLayout.isRefreshing = false
    }

    private fun setRecyclerView() { // TODO: Board
        val statusBarSize = UIUtils.makeDP(this, 24f)
        boardRecyclerView.run {
            layoutManager = LinearLayoutManager(this@MainActivity)
            LastAdapter(boards, BR.item)
                    .map<Board, ItemBoardBinding>(R.layout.item_board) {

                        onBind {
                            val board = boards[it.adapterPosition]

                            it.binding.feedGridView.run {
                                imageUrls.clear()
                                imageUrls.addAll(board.pictures)
                                viewUpdate()
                            }

                            requestManager
                                    .load(NetworkHelper.url + board.writer.profile)
                                    .signature(ObjectKey(NetworkHelper.url + board.writer.profile))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(it.itemView.writerImage)

                            it.binding.run {
                                writeDate.text = dateFormat.format(board.createDate)

                                var commentCount = 0
                                board.comments.forEach { commentCount += 1 + it.reComment.size }
                                commentCountText.text = "$commentCount+"
                                likeCountText.text = "${board.likes.size}+"

                                likeButton.imageResource =
                                        if (board.likes.any { it == DataHelper.datas?.user?._id }) R.drawable.ic_favorite
                                        else R.drawable.ic_favorite_empty

                                subscribeButton.alpha = if (board.writer.followers.any { it == DataHelper.datas!!.user._id }) 1f else 0.3f

                                likeButton.onClick { _ ->
                                    DataHelper.datas!!.mainBoards[it.adapterPosition] = boards[it.adapterPosition].apply {
                                        val isLike = likes.any { it == DataHelper.datas!!.user._id }
                                        if (isLike) {
                                            likes.remove(DataHelper.datas!!.user._id)
                                        } else {
                                            likes.add(DataHelper.datas!!.user._id)
                                        }
                                        likeCountText.text = "${likes.size}+"
                                    }

                                    likeButton.imageResource =
                                            if (board.likes.filter { it == DataHelper.datas?.user?._id }.isNotEmpty()) R.drawable.ic_favorite
                                            else R.drawable.ic_favorite_empty

                                    val userToken = DataHelper.datas?.token!!
                                    async(CommonPool) { NetworkHelper.retrofitInstance.likeBoard(userToken, board._id).execute() }.await().apply {

                                        if (!isSuccessful)
                                            return@onClick

                                        val json: JSONObject = JSONObject(body()!!.string())
                                        val isSuccess = json.getBoolean("success")

                                        if (!isSuccess) {
                                            return@apply
                                        }
                                    }
                                }

                                luvButton.onClick {
                                    LuvDonateDialog(this@MainActivity).show()
                                }

                                subscribeButton.onClick { _ ->
                                    DataHelper.datas!!.mainBoards[it.adapterPosition].writer = boards[it.adapterPosition].writer.apply {

                                        val isSubscribe = followers.any { it == DataHelper.datas!!.user._id }
                                        if (isSubscribe) {
                                            followers.remove(DataHelper.datas!!.user._id)
                                        } else {
                                            followers.add(DataHelper.datas!!.user._id)
                                        }
                                    }

                                    subscribeButton.alpha = if (board.writer.followers.any { it == DataHelper.datas!!.user._id }) 1f else 0.3f


                                    val userToken = DataHelper.datas?.token!!
                                    val toId = board.writer._id

                                    async(CommonPool) {
                                        if (board.writer.followers.any { it == DataHelper.datas!!.user._id })
                                            NetworkHelper.retrofitInstance.followUser(userToken, toId).execute()
                                        else
                                            NetworkHelper.retrofitInstance.unFollowUser(userToken, toId).execute()
                                    }.await().apply {
                                        if (!isSuccessful)
                                            return@onClick

                                        val json: JSONObject = JSONObject(body()!!.string())
                                        val isSuccess = json.getBoolean("success")
                                        if (!isSuccess) {
                                            return@apply
                                        }
                                    }
                                }
                            }
                            it.itemView.onClick { _ ->
                                startActivity<DetailFeedActivity>("_id" to board._id)
                            }
                        }
                        onRecycle {
                            it.binding.feedGridView.imageViews.clear()
                        }

                        onCreate {
                            requestManager
                                    .load(NetworkHelper.url + DataHelper.datas?.user?.profile)
                                    .into(it.itemView.profileImage)

                            it.binding.commentEditText.isEnabled = false
                            it.binding.feedGridView.requestManager = this@MainActivity.requestManager
                        }
                    }
                    .into(this)
            setBackDropHelper()
            setBottomBarClickHelper()
            this@run.setOnTouchListener { _, event ->
                backdropHelper.onTouch(event) or bottomBarClickHelper.onTouch(event)
            }
            this@run.onInterceptTouchEvent = { event ->
                val y = event.rawY
                y > bottomTabBar.y || y < (topHeight * 3 + statusBarSize)
            }
        }
    }

    private fun setBottomBarClickHelper() { // bottomBarItem onClicks
        bottomBarClickHelper = BottomBarClickHelper(bottomItems.toTypedArray()).apply {
            onClickItem = { _, i ->
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

    private fun setBackDropHelper() {
        keyboardHelper = KeyboardHelper(this@MainActivity)
        backdropHelper = BackdropHelper(this@MainActivity, boardRecyclerView, keyboardHelper, swipeRefreshLayout)
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START)
        } else
            super.onBackPressed()
    }
}
