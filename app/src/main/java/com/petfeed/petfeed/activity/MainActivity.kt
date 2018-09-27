package com.petfeed.petfeed.activity

import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
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
import okhttp3.ResponseBody
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        requestManager
                .load(NetworkHelper.url + DataHelper.datas!!.user.profile)
                .into(profileImage)
    }

    var selectedPage = 0
    private fun setViewPager() {
        bottomItems.run {
            add(bottomTabItem1)
            add(bottomTabItem2)
            add(bottomTabItem3)
            add(bottomTabItem4)
            add(bottomTabItem5)
        }
        viewPager.adapter = MainPagerAdapter(supportFragmentManager)
        viewPager.currentItem = 0
    }

    private fun setBottomItemAlpha() {
        bottomItems.filterIndexed { i, _ -> i != 2 }.forEachIndexed { i, v ->
            v.alpha = if (i == selectedPage) 1.0f else 0.3f
        }
    }

    private suspend fun getBoards() {
        if (!NetworkHelper.checkNetworkConnected(this)) {
            UIUtils.printNetworkCaution(this)
            swipeRefreshLayout.isRefreshing = false
            return
        }
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
                it.lowPictures.forEachIndexed { index, s ->
                    it.lowPictures[index] = NetworkHelper.url + s
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

                        onBind { it ->
                            val position = it.adapterPosition
                            val board = boards[position]

                            it.binding.feedGridView.run {
                                imageUrls.clear()
                                imageUrls.addAll(board.lowPictures)
                                viewUpdate()
                            }

                            requestManager
                                    .load(NetworkHelper.url + board.writer.profile)
                                    .signature(ObjectKey(NetworkHelper.url + board.writer.profile))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .override(100, 100)
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

//                                hashTagContainer.removeAllViews()
//                                board.hashTags.forEach {
//                                    val hashTag = TextView(this@MainActivity).apply {
//                                        setTextSize(TypedValue.COMPLEX_UNIT_PX, UIUtils.makeDP(context, 12f))
//                                        setLineSpacing(UIUtils.makeDP(context, 12f), 0f)
//                                        text = it
//                                        typeface = ResourcesCompat.getFont(this@MainActivity, R.font.nanum_square_round_bold)
//                                        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
//                                    }
//                                    hashTagContainer.addView(hashTag)
//                                }

                                likeButton.onClick { _ ->
                                    DataHelper.datas!!.mainBoards[position] = boards[position].apply {
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
                                    if (!NetworkHelper.checkNetworkConnected(this@MainActivity)) {
                                        UIUtils.printNetworkCaution(this@MainActivity)
                                        return@onClick
                                    }
                                    DataHelper.datas!!.mainBoards[position].writer = boards[position].writer.apply {

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
                                menuButton.onClick {
                                    val popup = PopupMenu(this@MainActivity, it!!)
                                    popup.inflate(R.menu.board)
                                    popup.setOnMenuItemClickListener { item ->
                                        when (item.itemId) {
                                            R.id.delete -> {
                                                if (board.writer._id != DataHelper.datas!!.user._id) {
                                                    toast("권한이 없습니다.")
                                                    return@setOnMenuItemClickListener true
                                                }
                                                if (!NetworkHelper.checkNetworkConnected(this@MainActivity)) {
                                                    UIUtils.printNetworkCaution(this@MainActivity)
                                                    return@setOnMenuItemClickListener true
                                                }
                                                val userToken = DataHelper.datas!!.token
                                                NetworkHelper.retrofitInstance.deleteBoard(userToken, board._id).enqueue(object : Callback<ResponseBody> {
                                                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                                    }

                                                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                                        if (!response.isSuccessful)
                                                            return

                                                        val json: JSONObject = JSONObject(response.body()!!.string())
                                                        val isSuccess = json.getBoolean("success")
                                                        if (!isSuccess)
                                                            return
                                                        DataHelper.datas!!.mainBoards.removeAt(position)
                                                        boardRecyclerView.adapter?.notifyItemRemoved(position)
                                                    }
                                                })
                                                true
                                            }
                                            R.id.report -> {
                                                true
                                            }
                                            else -> false
                                        }
                                    }
                                    popup.show()
                                }
                            }
                            it.itemView.onClick { _ ->
                                startActivity<DetailFeedActivity>("_id" to board._id)
                            }
                        }
                        onRecycle {
                            it.binding.feedGridView.imageViews.clear()
                            it.binding.feedGridView.imageViews.forEach {
                                requestManager.clear(it)
                            }
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
                        selectedPage = i
                        backdropHelper.down()
                    }
                    2 -> {
                        startActivity<WriteActivity>()
                        overridePendingTransition(R.anim.slide_in_up, android.R.anim.fade_out)
                    }
                    else -> {
                        viewPager.currentItem = i - 1
                        selectedPage = i - 1
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
            if (ratio <= 0.5f)
                bottomItems.filterIndexed { i, _ -> i != 2 }.forEach { v ->
                    v.alpha = 0.3f
                }
            else
                setBottomItemAlpha()

            viewPager.alpha = ratio
            contentContainer.backgroundColor = UIUtils.ratioARGB(brown, 1 - ratio)
        }
    }

    private var isCloseBack = false
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(Gravity.START) -> drawerLayout.closeDrawer(Gravity.START)
            isCloseBack -> super.onBackPressed()
            else -> {
                toast("한번 더 누르시면 종료 됩니다.")
                isCloseBack = true
                Handler().postDelayed({
                    isCloseBack = false
                }, 2000)
            }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }
}
