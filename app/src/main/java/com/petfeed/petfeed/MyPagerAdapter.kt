package com.petfeed.petfeed

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.petfeed.petfeed.fragment.SearchFragment

class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getItem(p0: Int): Fragment = SearchFragment.newInstance()

    override fun getCount(): Int = 5

}