package com.petfeed.petfeed.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.R
import com.petfeed.petfeed.activity.LogActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
import java.util.*

class ProfileFragment : Fragment() {

    var boards = ArrayList<Any>().apply {
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
        add("asdf")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerView()
        log_button.onClick {
            startActivity<LogActivity>()
        }
    }


    fun setRecyclerView() {
        board_recycler_view.run {
            LastAdapter(boards, BR.item)
                    .map<String>(R.layout.item_board)
                    .into(this)
            layoutManager = LinearLayoutManager(context)
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
