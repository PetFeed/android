package com.petfeed.petfeed.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.petfeed.petfeed.fragment.CardFragment
import com.petfeed.petfeed.fragment.EmptyCardFragment

class CardPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = if (position == 0) EmptyCardFragment.newInstance() else CardFragment.newInstance()
    override fun getCount(): Int = 3
    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE
}