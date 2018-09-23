package com.petfeed.petfeed.fragment


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.github.nitrico.lastadapter.LastAdapter
import com.google.gson.Gson
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.GlideApp
import com.petfeed.petfeed.GlideRequests
import com.petfeed.petfeed.R
import com.petfeed.petfeed.activity.DetailFeedActivity
import com.petfeed.petfeed.activity.GalleryActivity
import com.petfeed.petfeed.activity.LogActivity
import com.petfeed.petfeed.activity.SettingActivity
import com.petfeed.petfeed.databinding.ItemProfileBoardBinding
import com.petfeed.petfeed.model.Board
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.model.User
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_profile_board.view.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ProfileFragment : Fragment() {

    val requestCode = 1
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
        requestManager
                .load(imageUrl)
                .into(profileImage)
        userName.text = user.nickname
        logButton.onClick {
            startActivity<LogActivity>()
        }
        settingButton.onClick {
            startActivity<SettingActivity>()
        }
        profileImage.onClick {
            startActivityForResult<GalleryActivity>(requestCode, "type" to GalleryActivity.ONLYONE)
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
                                imageUrls.addAll(board.lowPictures)
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

    private suspend fun changeProfile(file: File) {
        val body = RequestBody.create(MediaType.parse("image/*"), file)
        val picture = MultipartBody.Part.createFormData("profile", file.name, body)

//        NetworkHelper.retrofitInstance.updateUser(DataHelper.datas!!.token, picture, HashMap<String, String>().toMap()).enqueue(object: Callback<ResponseBody> {
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                t.printStackTrace()
//            }
//
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//            }
//        })

        async(CommonPool) { NetworkHelper.retrofitInstance.updateUser(DataHelper.datas!!.token, picture, HashMap<String, String>().toMap()).execute() }.await().apply {
            Log.e("profile", "asdfadsf")

            if (!isSuccessful)
                return

            val json: JSONObject = JSONObject(body()!!.string())
            val isSuccess = json.getBoolean("success")

            Log.e("profile", "asdfasdfasdfaf")

            if (!isSuccess) {
                return
            }
            Log.e("profile", "성공")
        }

        async(CommonPool) { NetworkHelper.retrofitInstance.getUser(DataHelper.datas!!.token).execute() }.await().apply {
            if (!isSuccessful)
                return

            val json: JSONObject = JSONObject(body()!!.string())
            val isSuccess = json.getBoolean("success")

            if (!isSuccess)
                return

            val user = Gson().fromJson(json.getString("user"), User::class.java)
            DataHelper.datas?.user = user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GalleryActivity.ONLYONE && resultCode == RESULT_OK) {
            val path = data!!.getStringArrayExtra("paths").first()
            val file = File(path)
            async(CommonPool) { changeProfile(file) }
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
