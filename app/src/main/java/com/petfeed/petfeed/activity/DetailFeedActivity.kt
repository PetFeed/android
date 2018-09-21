package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.github.nitrico.lastadapter.LastAdapter
import com.google.gson.Gson
import com.petfeed.petfeed.*
import com.petfeed.petfeed.databinding.ItemCommentBinding
import com.petfeed.petfeed.databinding.ItemCommentCommentBinding
import com.petfeed.petfeed.model.Board
import com.petfeed.petfeed.model.Comment
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.DataHelper
import com.petfeed.petfeed.util.UIUtils
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_detail_feed.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.textChangedListener
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class DetailFeedActivity : AppCompatActivity() {

    val comments = ArrayList<Any>()

    var board: Board? = null
    val dateFormat = SimpleDateFormat("MMM d일 a KK시 mm분", Locale.KOREAN)
    var sendCommentId = ""
    lateinit var requestManager: GlideRequests
    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white1, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_feed)

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
                commentEditText.setText("")
            } else {
                LuvDonateDialog(this@DetailFeedActivity).show()
            }
        }
    }

    private suspend fun initBoard() {
        board = Board()
        val userToken = DataHelper.datas!!.token
        val boardId = intent.getStringExtra("_id")
        async(CommonPool) { NetworkHelper.retrofitInstance.getBoardById(userToken, boardId).execute() }.await().apply {
            if (!isSuccessful)
                return
            val json: JSONObject = JSONObject(body()!!.string())
            val isSuccess = json.getBoolean("success")

            if (!isSuccess)
                return

            val board = Gson().fromJson(json.getString("data"), Board::class.java)
            this@DetailFeedActivity.board = board
        }

        comments.clear()
        board!!.comments.forEach {
            comments.add(it)
            if (it.reComment.isNotEmpty())
                it.reComment.forEach {
                    comments.add(Pair<Comment, String>(it, ""))
                }
        }
        board!!.pictures.forEachIndexed { index, s ->
            board!!.pictures[index] = NetworkHelper.url + s
        }
        sendCommentId = board!!._id
        commentRecyclerView?.adapter?.notifyDataSetChanged()

        feedGridView.imageUrls.clear()
        feedGridView.imageUrls.addAll(board!!.pictures)
        feedGridView.viewUpdate()
        writeDate.text = dateFormat.format(board!!.createDate)
        contents.text = board!!.contents
        requestManager
                .load(NetworkHelper.url + board!!.writer.profile)
                .into(writerProfileImage)
        writerName.text = board!!.writer.nickname
        topContainer.scrollTo(0, 0)
    }

    private fun setRecyclerView() {
        commentRecyclerView.run {
            LastAdapter(comments, BR.item)
                    .map<Comment, ItemCommentBinding>(R.layout.item_comment) {
                        onBind {
                            val comment = (comments[it.adapterPosition] as Comment)
                            it.binding.writerName.text = comment.writer?.nickname
                            it.binding.content.text = comment.content
//                            it.binding.writeTime.text =
                            requestManager
                                    .load(NetworkHelper.url + comment.writer!!.profile)
                                    .into(it.binding.writerImage)
                            it.binding.writeTime.text = UIUtils.getLaterText(comment.createDate!!)
                        }
                        onClick {
                            val comment = (comments[it.adapterPosition] as Comment)
                            sendCommentId = comment._id
                        }
                    }
                    .map<Pair<Comment, String>, ItemCommentCommentBinding>(R.layout.item_comment_comment) {
                        onBind {
                            val comment = (comments[it.adapterPosition] as Pair<Comment, String>).first
                            it.binding.writerName.text = comment.writer?.nickname
                            it.binding.content.text = comment.content
//                            it.binding.writeTime.text =
                            requestManager
                                    .load(NetworkHelper.url + comment.writer!!.profile)
                                    .into(it.binding.writerImage)
                            it.binding.writeDate.text = UIUtils.getLaterText(comment.createDate!!)
                        }
                        onClick {
                            val comment = (comments[it.adapterPosition] as Pair<Comment, String>).first
                            sendCommentId = comment.parentId!!
                        }
                    }
                    .into(this)
            layoutManager = LinearLayoutManager(this@DetailFeedActivity)
        }
    }
}
