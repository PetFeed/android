package com.petfeed.petfeed

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.petfeed.petfeed.fragment.MarketFragment
import com.petfeed.petfeed.fragment.NotificationFragment
import com.petfeed.petfeed.fragment.ProfileFragment
import com.petfeed.petfeed.fragment.SearchFragment

class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> SearchFragment.newInstance()
        1 -> NotificationFragment.newInstance()
        2 -> ProfileFragment.newInstance()
        3 -> MarketFragment.newInstance()
        else -> SearchFragment.newInstance()
    }

    override fun getCount(): Int = 4

}