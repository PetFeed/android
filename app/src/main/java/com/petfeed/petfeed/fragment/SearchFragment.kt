package com.petfeed.petfeed.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.github.nitrico.lastadapter.LastAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.petfeed.petfeed.*
import com.petfeed.petfeed.activity.DetailFeedActivity
import com.petfeed.petfeed.activity.MainActivity
import com.petfeed.petfeed.databinding.ItemBoardBinding
import com.petfeed.petfeed.model.Board
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.childrenRecursiveSequence
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {
    var boards = ArrayList<Board>()

    lateinit var requestManager: GlideRequests
    val dateFormat = SimpleDateFormat("MMM d일 a KK시 mm분", Locale.KOREAN)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requestManager = GlideApp.with(context!!)

        setRecyclerView()
        scrollView.childrenRecursiveSequence().forEach {
            if (it.id == R.id.boardRecyclerView) {
                return@forEach
            }
            it.onClick { _ ->
                hideKeyBoard()
            }
        }
        scrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            hideKeyBoard()
        }

        async(UI) {
            getBoards()
        }
    }

    private suspend fun getBoards() {
        async(CommonPool) { NetworkHelper.retrofitInstance.getAllBoards(DataHelper.datas!!.token).execute() }.await().apply {

            if (!isSuccessful) {
                return@apply
            }

            val json: JSONObject = JSONObject(body()!!.string())
            val isSuccess = json.getBoolean("success")

            if (!isSuccess) {
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
    }

    private fun hideKeyBoard() {
        (this@SearchFragment.context as MainActivity).keyboardHelper.run {
            if (isKeyboardVisible()) {
                hideKeyboard()
            }
        }
    }

    private fun setRecyclerView() {// TODO: Board

        boardRecyclerView.run {
            layoutManager = LinearLayoutManager(context)
            LastAdapter(boards, BR.item)
                    .map<Board, ItemBoardBinding>(R.layout.item_board) {

                        onBind { it ->
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
                                            if (board.likes.any { it == DataHelper.datas?.user?._id }) R.drawable.ic_favorite
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

                                luvButton.onClick {
                                    LuvDonateDialog(this@SearchFragment.context!!).show()
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
                            it.binding.feedGridView.requestManager = this@SearchFragment.requestManager
                        }
                    }
                    .into(this)
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
