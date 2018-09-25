package com.petfeed.petfeed.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.github.nitrico.lastadapter.LastAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.GlideApp
import com.petfeed.petfeed.R
import com.petfeed.petfeed.activity.DetailFeedActivity
import com.petfeed.petfeed.databinding.ItemNotificationContentBinding
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.model.Log
import com.petfeed.petfeed.util.PrefManager
import com.petfeed.petfeed.util.UIUtils
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.fragment_notification.*
import okhttp3.ResponseBody
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.textColorResource
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeFragment : Fragment() {

    val notices = ArrayList<Log>()

    val items = ArrayList<Any>()
    lateinit var requestManager: RequestManager
    lateinit var prefManager: PrefManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requestManager = GlideApp.with(this)
        prefManager = PrefManager(context!!)
        initNotification()
        setRecyclerView()
    }

    private fun initNotification() {
        NetworkHelper.retrofitInstance.getNotice(DataHelper.datas!!.token).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val json = JSONObject(response.body()!!.string())
                val success = json.getBoolean("success")
                if (!success)
                    return

                val notices = Gson().fromJson<ArrayList<Log>>(
                        json.getJSONObject("data").getString("logs"),
                        object : TypeToken<ArrayList<Log>>() {}.type)

                this@NoticeFragment.notices.clear()
                this@NoticeFragment.notices.addAll(notices.reversed())

                val connTime = DataHelper.datas!!.connTime
                android.util.Log.e("asdf", "${connTime}  ${notices[0].date.time}")
                var b = true
                if (notices.isNotEmpty() && notices[0].date.time > connTime)
                    items.add("새 알림")
                notices.forEach {
                    it.isChecked = it._id in prefManager.checkedLogs
                    if (connTime > it.date.time && b) {
                        b = false
                        items.add("이전 알림")
                    }
                }
                items.addAll(notices)
                notificationRecyclerView.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun setRecyclerView() {
        notificationRecyclerView.layoutManager = LinearLayoutManager(context)
        LastAdapter(items, BR.item)
                .map<String>(R.layout.item_notification_title)
                .map<Log, ItemNotificationContentBinding>(R.layout.item_notification_content) {
                    onBind {
                        val log = items[it.adapterPosition] as Log
                        it.binding.content.text = log.text
                        requestManager
                                .load(NetworkHelper.url + log.fromUser.profile)
                                .into(it.binding.profileImage)
                        it.binding.time.text = UIUtils.getLaterText(log.date)
                        it.binding.container.backgroundColorResource = if (log.isChecked!!) R.color.brown1_10 else android.R.color.white
                        it.binding.time.textColorResource = if (log.isChecked!!) R.color.brown1 else R.color.black1_30
                    }
                    onClick {
                        val log = items[it.adapterPosition] as Log
                        prefManager.addCheckedLogs(log._id)
                        (items[it.adapterPosition] as Log).isChecked = true
                        notificationRecyclerView.adapter?.notifyItemChanged(it.adapterPosition)

                        if (log.type == "Board")
                            startActivity<DetailFeedActivity>("_id" to log.dataId)
                    }
                }
                .into(notificationRecyclerView)
    }

    companion object {
        fun newInstance() = NoticeFragment()
    }
}