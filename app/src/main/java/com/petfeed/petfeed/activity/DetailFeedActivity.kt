package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.View
import com.github.nitrico.lastadapter.LastAdapter
import com.google.gson.Gson
import com.petfeed.petfeed.*
import com.petfeed.petfeed.databinding.ItemCommentBinding
import com.petfeed.petfeed.databinding.ItemCommentCommentBinding
import com.petfeed.petfeed.model.Board
import com.petfeed.petfeed.model.Comment
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.KeyboardHelper
import com.petfeed.petfeed.util.UIUtils
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_detail_feed.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okhttp3.ResponseBody
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.textChangedListener
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DetailFeedActivity : AppCompatActivity() {

    val comments = ArrayList<Any>()

    var board: Board? = null
    val dateFormat = SimpleDateFormat("MMM d일 a KK시 mm분", Locale.KOREAN)
    var sendCommentId = ""
    var sendCommentToUserName = ""
    var isCommentInfoVisible = false
        set(value) {
            field = value
            reCommentInfoContainer.visibility = if (value) View.VISIBLE else View.GONE
            divLine.visibility = if (value) View.VISIBLE else View.GONE
            reCommentToUserText.text = sendCommentToUserName + "님에게 댓글을 다는 중"
            if (!value) sendCommentId = board!!._id
            if (value) {
                commentEditText.requestFocus()
                keyboardHelper.showKeyboard()
            }
        }
    lateinit var keyboardHelper: KeyboardHelper
    lateinit var requestManager: GlideRequests

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white1, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_feed)

        keyboardHelper = KeyboardHelper(this)
        requestManager = GlideApp.with(this)
        feedGridView.requestManager = requestManager
        setRecyclerView()
        backButton.onClick {
            finish()
        }

        async(UI) { initBoard() }

        requestManager
                .load(NetworkHelper.url + DataHelper.datas!!.user.profile)
                .into(profileImage)

        reCommentCloseButton.onClick {
            isCommentInfoVisible = false
            sendCommentId = ""
            sendCommentToUserName = ""
        }

        commentEditText.textChangedListener {
            onTextChanged { s, _, _, _ ->
                if (s == null || s.isEmpty()) {
                    luvButton.imageResource = R.drawable.ic_luv
                } else {
                    luvButton.imageResource = R.drawable.ic_send
                }
            }
        }

        luvButton.onClick {
            if (!NetworkHelper.checkNetworkConnected(this@DetailFeedActivity)) {
                UIUtils.printNetworkCaution(this@DetailFeedActivity)
                return@onClick
            }
            if (commentEditText.text.toString() != "") {
                async(CommonPool) {
                    NetworkHelper.retrofitInstance.postComment(DataHelper.datas!!.token,
                            sendCommentId,
                            commentEditText.text.toString(),
                            if (sendCommentId == board!!._id) "" else "re").execute()


                }.await().apply {
                    if (!isSuccessful)
                        toast("네트워크 오류가 발생했습니다.")
                    async(UI) { initBoard() }
                }
                isCommentInfoVisible = false
                commentEditText.setText("")
                keyboardHelper.hideKeyboard()
            } else {
//                LuvDonateDialog(this@DetailFeedActivity).show()
            }
        }

        likeButton.onClick { _ ->
            if (!NetworkHelper.checkNetworkConnected(this@DetailFeedActivity)) {
                UIUtils.printNetworkCaution(this@DetailFeedActivity)
                return@onClick
            }
            board?.apply {
                val isLike = likes.any { it == DataHelper.datas!!.user._id }
                if (isLike) {
                    likes.remove(DataHelper.datas!!.user._id)
                } else {
                    likes.add(DataHelper.datas!!.user._id)
                }
                likeCountText.text = "${likes.size}+"
            }
            likeButton.imageResource =
                    if (board != null && board!!.likes.filter { it == DataHelper.datas?.user?._id }.isNotEmpty()) R.drawable.ic_favorite
                    else R.drawable.ic_favorite_empty

            val userToken = DataHelper.datas?.token!!
            async(CommonPool) { NetworkHelper.retrofitInstance.likeBoard(userToken, board!!._id).execute() }.await().apply {

                if (!isSuccessful)
                    return@onClick

                val json: JSONObject = JSONObject(body()!!.string())
                val isSuccess = json.getBoolean("success")

                if (!isSuccess) {
                    return@apply
                }
            }
        }

        feedGridView.onClick {
            startActivity<DetailImageActivity>("images" to board!!.pictures.toTypedArray())
        }


        subscribeButton.onClick { _ ->
            if (board == null)
                return@onClick
            if (!NetworkHelper.checkNetworkConnected(this@DetailFeedActivity)) {
                UIUtils.printNetworkCaution(this@DetailFeedActivity)
                return@onClick
            }
            val mainBoard = DataHelper.datas!!.mainBoards.first { it._id == board!!._id }
            mainBoard.writer.run {
                val isSubscribe = followers.any { it == DataHelper.datas!!.user._id }
                if (isSubscribe) {
                    followers.remove(DataHelper.datas!!.user._id)
                } else {
                    followers.add(DataHelper.datas!!.user._id)
                }
            }
            board!!.writer.run {
                val isSubscribe = followers.any { it == DataHelper.datas!!.user._id }
                if (isSubscribe) {
                    followers.remove(DataHelper.datas!!.user._id)
                } else {
                    followers.add(DataHelper.datas!!.user._id)
                }
            }
            subscribeButton.alpha = if (mainBoard.writer.followers.any { it == DataHelper.datas!!.user._id }) 1f else 0.3f


            val userToken = DataHelper.datas?.token!!
            val toId = board!!.writer._id

            async(CommonPool) {
                if (board!!.writer.followers.any { it == DataHelper.datas!!.user._id })
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
            val popup = PopupMenu(this@DetailFeedActivity, it!!)
            popup.inflate(R.menu.board)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        if (board == null)
                            return@setOnMenuItemClickListener true
                        if (board!!.writer._id != DataHelper.datas!!.user._id) {
                            toast("권한이 없습니다.")
                            return@setOnMenuItemClickListener true
                        }
                        if (!NetworkHelper.checkNetworkConnected(this@DetailFeedActivity)) {
                            UIUtils.printNetworkCaution(this@DetailFeedActivity)
                            return@setOnMenuItemClickListener true
                        }
                        val userToken = DataHelper.datas!!.token
                        NetworkHelper.retrofitInstance.deleteBoard(userToken, board!!._id).enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            }

                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (!response.isSuccessful)
                                    return

                                val json: JSONObject = JSONObject(response.body()!!.string())
                                val isSuccess = json.getBoolean("success")
                                if (!isSuccess)
                                    return
                                val mainBoard = DataHelper.datas!!.mainBoards.first { it._id == board!!._id }
                                DataHelper.datas!!.mainBoards.remove(mainBoard)
                                finish()
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

    private fun initBoard() {
        if (!NetworkHelper.checkNetworkConnected(this)) {
            UIUtils.printNetworkCaution(this)
            return
        }
        board = Board()
        val userToken = DataHelper.datas!!.token
        val boardId = intent.getStringExtra("_id")
        NetworkHelper.retrofitInstance.getBoardById(userToken, boardId).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                toast("존재하지 않는 게시물 입니다.")
                finish()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!response.isSuccessful)
                    return
                val json: JSONObject = JSONObject(response.body()!!.string())
                val isSuccess = json.getBoolean("success")

                if (!isSuccess)
                    return

                val board = Gson().fromJson(json.getString("data"), Board::class.java)
                this@DetailFeedActivity.board = board
                if (board == null) {
                    toast("존재하지 않는 게시물 입니다.")
                    finish()
                    return
                }
                comments.clear()
                board!!.comments.forEach {
                    comments.add(it)
                    if (it.reComment.isNotEmpty())
                        it.reComment.forEach {
                            comments.add(Pair<Comment, String>(it, ""))
                        }
                }
                board.pictures.forEachIndexed { index, s ->
                    board.pictures[index] = NetworkHelper.url + s
                }
                board.lowPictures.forEachIndexed { index, s ->
                    board.lowPictures[index] = NetworkHelper.url + s
                }
                subscribeButton.alpha = if (board.writer.followers.any { it == DataHelper.datas!!.user._id }) 1f else 0.3f
                sendCommentId = board._id
                commentRecyclerView?.adapter?.notifyDataSetChanged()

                feedGridView.run {
                    imageUrls.clear()
                    imageUrls.addAll(board.lowPictures)
                    viewUpdate()
                }

                var commentCount = 0
                board.comments.forEach { commentCount += 1 + it.reComment.size }
                commentCountText.text = "$commentCount+"
                likeCountText.text = "${board.likes.size}+"

                likeButton.imageResource =
                        if (board.likes.any { it == DataHelper.datas?.user?._id }) R.drawable.ic_favorite
                        else R.drawable.ic_favorite_empty

                writeDate.text = dateFormat.format(board.createDate)
                contents.text = board.contents

                requestManager
                        .load(NetworkHelper.url + board.writer.profile)
                        .into(writerProfileImage)

                writerName.text = board.writer.nickname
                topContainer.scrollTo(0, 0)
            }
        })
    }

    private fun setRecyclerView() {
        commentRecyclerView.run {
            LastAdapter(comments, BR.item)
                    .map<Comment, ItemCommentBinding>(R.layout.item_comment) {
                        onBind {
                            val comment = (comments[it.adapterPosition] as Comment)
                            it.binding.run {
                                writerName.text = comment.writer?.nickname
                                content.text = comment.content
                                writeTime.text = UIUtils.getLaterText(comment.createDate!!)

                                requestManager
                                        .load(NetworkHelper.url + comment.writer!!.profile)
                                        .into(writerImage)
                            }
                        }
                        onClick {
                            val comment = (comments[it.adapterPosition] as Comment)
                            sendCommentId = comment._id
                            sendCommentToUserName = comment.writer!!.nickname
                            isCommentInfoVisible = true
                        }
                    }
                    .map<Pair<Comment, String>, ItemCommentCommentBinding>(R.layout.item_comment_comment) {
                        onBind {
                            val comment = (comments[it.adapterPosition] as Pair<Comment, String>).first
                            it.binding.run {
                                writerName.text = comment.writer?.nickname
                                content.text = comment.content
                                writeDate.text = UIUtils.getLaterText(comment.createDate!!)

                                requestManager
                                        .load(NetworkHelper.url + comment.writer!!.profile)
                                        .into(writerImage)
                            }
                        }
                        onClick {
                            val comment = (comments[it.adapterPosition] as Pair<Comment, String>).first
                            var i = it.adapterPosition
                            while (true) {
                                if (comments[i] is Comment) {
                                    sendCommentId = (comments[i] as Comment)._id
                                    break
                                }
                                i--
                            }
                            sendCommentToUserName = comment.writer!!.nickname
                            isCommentInfoVisible = true
                        }
                    }
                    .into(this)
            layoutManager = LinearLayoutManager(this@DetailFeedActivity)
        }
    }
}
