package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.github.nitrico.lastadapter.BR
import com.github.nitrico.lastadapter.LastAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.petfeed.petfeed.GlideApp
import com.petfeed.petfeed.GlideRequests
import com.petfeed.petfeed.R
import com.petfeed.petfeed.databinding.ItemLogContentBinding
import com.petfeed.petfeed.model.DataHelper
import com.petfeed.petfeed.model.Log
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.PrefManager
import com.petfeed.petfeed.util.UIUtils
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.activity_log.*
import okhttp3.ResponseBody
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class LogActivity : AppCompatActivity() {

    lateinit var prefManager: PrefManager
    lateinit var requestManager: GlideRequests

    val items = ArrayList<Any>()
    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white2, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        prefManager = PrefManager(this)
        requestManager = GlideApp.with(this)
        setToolbar()
        initActivity()
        setRecyclerView()
    }

    private fun initActivity() {
        if (!NetworkHelper.checkNetworkConnected(this)) {
            UIUtils.printNetworkCaution(this)
            return
        }
        NetworkHelper.retrofitInstance.getActivity(DataHelper.datas!!.token).enqueue(object : Callback<ResponseBody> {
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

                val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN)
                var lastDate = ""
                val myId = DataHelper.datas!!.user._id
                notices.forEach {
                    it.isChecked = it._id in prefManager.checkedLogs
                    val newDate = dateFormat.format(it.date)
                    if (it.toId == myId)
                        return@forEach
                    if (lastDate != newDate) {
                        items.add(newDate)
                        lastDate = newDate
                    }
                    items.add(it)
                }
                logRecyclerView.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun setRecyclerView() {
        logRecyclerView.layoutManager = LinearLayoutManager(this)
        LastAdapter(items, BR.item)
                .map<String>(R.layout.item_notification_title)
                .map<Log, ItemLogContentBinding>(R.layout.item_log_content) {
                    onBind {
                        val log = items[it.adapterPosition] as Log
                        it.binding.content.text = log.text
                        it.binding.container.backgroundColorResource = if (log.isChecked!!) R.color.brown1_10 else android.R.color.white

                        it.binding.activityImage.imageResource = if (log.text.contains(Regex("게시물을 좋아합니다.$")))
                            R.drawable.ic_favorite else R.drawable.ic_subscribe
                    }
                    onClick {
                        val log = items[it.adapterPosition] as Log
                        prefManager.addCheckedLogs(log._id)
                        (items[it.adapterPosition] as Log).isChecked = true
                        logRecyclerView.adapter?.notifyItemChanged(it.adapterPosition)

                        if (log.type == "Board")
                            startActivity<DetailFeedActivity>("_id" to log.dataId)
                    }
                }
                .into(logRecyclerView)
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(ContextCompat.getDrawable(this@LogActivity, R.drawable.ic_back))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
