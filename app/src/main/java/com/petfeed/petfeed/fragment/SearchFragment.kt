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
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

class SearchFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setRecyclerView()
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
        fun newInstance() = SearchFragment()
    }
}
