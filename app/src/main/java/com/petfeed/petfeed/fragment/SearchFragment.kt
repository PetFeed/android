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
import com.petfeed.petfeed.util.DataHelper
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_board.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.childrenRecursiveSequence
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

    private fun setRecyclerView() {

        boardRecyclerView.run {
            layoutManager = LinearLayoutManager(context)
            LastAdapter(boards, BR.item)
                    .map<Board, ItemBoardBinding>(R.layout.item_board) {

                        onBind {
                            val board = boards[it.adapterPosition]

                            it.itemView.feedGridView.imageUrls.clear()
                            it.itemView.feedGridView.imageUrls.addAll(board.pictures)
                            it.itemView.feedGridView.viewUpdate()

                            requestManager
                                    .load(NetworkHelper.url + board.writer.profile)
                                    .signature(ObjectKey(NetworkHelper.url + board.writer.profile))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(it.itemView.writerImage)

                            it.itemView.writeDate.text = dateFormat.format(board.createDate)
                            it.itemView.writerName.text = board.writer.nickname
                            it.itemView.onClick { _ ->
                                startActivity<DetailFeedActivity>("_id" to board._id)
                            }
                            it.itemView.luvButton.onClick {
                                LuvDonateDialog(this@SearchFragment.context!!)
                                        .show()
                            }
                        }
                        onRecycle {
                            it.itemView.feedGridView.imageViews.clear()
                        }

                        onCreate {
                            requestManager
                                    .load(NetworkHelper.url + DataHelper.datas?.user?.profile)
                                    .into(it.itemView.profileImage)

                            it.itemView.feedGridView.requestManager = this@SearchFragment.requestManager
                        }
                    }
                    .into(this)
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
