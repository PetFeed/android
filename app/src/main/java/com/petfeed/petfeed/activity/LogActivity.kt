package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR
import com.petfeed.petfeed.R
import com.petfeed.petfeed.databinding.ItemLogContentBinding
import kotlinx.android.synthetic.main.activity_log.*
import org.jetbrains.anko.backgroundColor

class LogActivity : AppCompatActivity() {

    val logs = ArrayList<Any>().apply {
        add("오늘")
        add(1)
        add(1)
        add(1)
        add("어제")
        add(1)
        add(1)
        add(1)
        add(1)
        add(1)
        add(1)
        add(1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        setToolbar()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        LastAdapter(logs, BR.item)
                .map<String>(R.layout.item_log_title)
                .map<Int, ItemLogContentBinding>(R.layout.item_log_content) {
                    onBind {
                        it.itemView.backgroundColor = ContextCompat.getColor(this@LogActivity, R.color.brown1_10)
                    }
                }
                .into(log_recycler_view)
        log_recycler_view.layoutManager = LinearLayoutManager(this@LogActivity)
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
