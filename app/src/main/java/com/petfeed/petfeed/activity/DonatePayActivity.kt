package com.petfeed.petfeed.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.petfeed.petfeed.R
import com.petfeed.petfeed.adapter.CardPagerAdapter
import com.petfeed.petfeed.util.ActivityUtils
import com.petfeed.petfeed.util.UIUtils
import kotlinx.android.synthetic.main.activity_donate_pay.*

class DonatePayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white2, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate_pay)

        cardPager.pageMargin = UIUtils.makeDP(this, 6f).toInt()
        cardPager.adapter = CardPagerAdapter(supportFragmentManager)
        setToolbar()
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
