package com.petfeed.petfeed.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.petfeed.petfeed.fragment.MarketFragment
import com.petfeed.petfeed.fragment.NoticeFragment
import com.petfeed.petfeed.fragment.ProfileFragment
import com.petfeed.petfeed.fragment.SearchFragment

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> SearchFragment.newInstance()
        1 -> NoticeFragment.newInstance()
        2 -> MarketFragment.newInstance()
        3 -> ProfileFragment.newInstance()
        else -> SearchFragment.newInstance()
    }

    override fun getCount(): Int = 4

}