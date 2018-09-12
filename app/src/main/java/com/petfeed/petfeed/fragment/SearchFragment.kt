package com.petfeed.petfeed.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.R
import com.petfeed.petfeed.activity.DetailFeedActivity
import com.petfeed.petfeed.activity.MainActivity
import com.petfeed.petfeed.databinding.ItemBoardBinding
import kotlinx.android.synthetic.main.fragment_search.*
import org.jetbrains.anko.childrenRecursiveSequence
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity
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
            LastAdapter(boards, BR.item)
                    .map<String, ItemBoardBinding>(R.layout.item_board) {
                        onClick {
                            it.itemView.onClick {
                                startActivity<DetailFeedActivity>()
                            }
                        }
                    }
                    .into(this)
            layoutManager = LinearLayoutManager(context)
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}
