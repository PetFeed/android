package com.petfeed.petfeed.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.*
import com.petfeed.petfeed.activity.DetailFeedActivity
import com.petfeed.petfeed.activity.LogActivity
import com.petfeed.petfeed.activity.SettingActivity
import com.petfeed.petfeed.databinding.ItemBoardBinding
import com.petfeed.petfeed.databinding.ItemProfileBoardBinding
import com.petfeed.petfeed.model.Board
import com.petfeed.petfeed.util.DataHelper
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_profile_board.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {

    var boards = ArrayList<Board>()
    var user = DataHelper.datas!!.user
    lateinit var requestManager: GlideRequests

    val dateFormat = SimpleDateFormat("MMM d일 a KK시 mm분", Locale.KOREAN)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestManager = GlideApp.with(this)

        setRecyclerView()
        val imageUrl = NetworkHelper.url + DataHelper.datas?.user?.profile
        Glide.with(context!!)
                .load(imageUrl)
                .into(profileImage)
        userName.text = user.nickname
        logButton.onClick {
            startActivity<LogActivity>()
        }
        settingButton.onClick {
            startActivity<SettingActivity>()
        }

        if (DataHelper.datas?.myBoards == null) {
            DataHelper.datas?.myBoards = ArrayList()
            DataHelper.datas?.myBoards!!.addAll(DataHelper.datas?.mainBoards?.filter { it.writer.id == DataHelper.datas?.user?.id }!!)

        }
        boards.clear()
        boards.addAll(DataHelper.datas?.myBoards!!)
        boardRecyclerView.adapter!!.notifyDataSetChanged()

    }

    private fun setRecyclerView() {// TODO: Board
        boardRecyclerView.run {
            LastAdapter(boards, BR.item)
                    .map<Board, ItemProfileBoardBinding>(R.layout.item_profile_board) {

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

                            }
                            it.itemView.onClick { _ ->
                                startActivity<DetailFeedActivity>("_id" to board._id)
                            }
                        }

                        onRecycle {
                            it.binding.feedGridView.imageViews.clear()
                        }

                        onCreate {
                            it.binding.feedGridView.requestManager = this@ProfileFragment.requestManager
                        }
                    }
                    .into(this)
            layoutManager = LinearLayoutManager(context)
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
