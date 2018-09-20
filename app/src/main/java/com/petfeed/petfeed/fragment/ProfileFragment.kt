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
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.GlideApp
import com.petfeed.petfeed.GlideRequests
import com.petfeed.petfeed.R
import com.petfeed.petfeed.activity.DetailFeedActivity
import com.petfeed.petfeed.activity.LogActivity
import com.petfeed.petfeed.activity.SettingActivity
import com.petfeed.petfeed.databinding.ItemProfileBoardBinding
import com.petfeed.petfeed.model.Board
import com.petfeed.petfeed.util.DataHelper
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_profile_board.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
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

    private fun setRecyclerView() {
        boardRecyclerView.run {
            LastAdapter(boards, BR.item)
                    .map<Board, ItemProfileBoardBinding>(R.layout.item_profile_board) {
                        onBind {
                            val board = boards[it.adapterPosition]

                            it.itemView.feedGridView.imageUrls.clear()
                            it.itemView.feedGridView.imageUrls.addAll(board.pictures)
                            it.itemView.feedGridView.viewUpdate()
                            it.binding.contents.text  = board.contents
                            requestManager
                                    .load(NetworkHelper.url + board.writer.profile)
                                    .signature(ObjectKey(NetworkHelper.url + board.writer.profile))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(it.itemView.writerImage)

                            it.itemView.writeDate.text = dateFormat.format(board.createDate)
                            it.itemView.writerName.text = board.writer.nickname
                            it.itemView.onClick { _ ->
                                startActivity<DetailFeedActivity>("boardId" to board.boardId)
                            }
                        }
                        onRecycle {
                            it.itemView.feedGridView.imageViews.clear()
                        }

                        onCreate {
                            it.itemView.feedGridView.requestManager = this@ProfileFragment.requestManager
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
