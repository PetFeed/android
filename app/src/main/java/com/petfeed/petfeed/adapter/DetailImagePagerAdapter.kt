package com.petfeed.petfeed.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import com.petfeed.petfeed.fragment.ImageFragment

class DetailImagePagerAdapter(fm: FragmentManager, val images: ArrayList<String>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = ImageFragment.newInstance(images[position])
    override fun getCount(): Int = images.size
    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE
}