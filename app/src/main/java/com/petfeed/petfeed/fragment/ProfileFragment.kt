package com.petfeed.petfeed.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.R
import com.petfeed.petfeed.activity.LogActivity
import com.petfeed.petfeed.activity.SettingActivity
import com.petfeed.petfeed.util.DataHelper
import com.petfeed.petfeed.util.network.NetworkHelper
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
import java.net.URI
import java.util.*

class ProfileFragment : Fragment() {

    var boards = ArrayList<Any>().apply {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerView()

        val imageUrl = NetworkHelper.url + DataHelper.datas?.user?.profile
        DataHelper.datas?.user?.profile
        Glide.with(context!!)
                .load(imageUrl)
                .into(profileImage)
        logButton.onClick {
            startActivity<LogActivity>()
        }
        settingButton.onClick {
            startActivity<SettingActivity>()
        }
    }


    private fun setRecyclerView() {
        boardRecyclerView.run {
            LastAdapter(boards, BR.item)
                    .map<String>(R.layout.item_profile_board)
                    .into(this)
            layoutManager = LinearLayoutManager(context)
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
