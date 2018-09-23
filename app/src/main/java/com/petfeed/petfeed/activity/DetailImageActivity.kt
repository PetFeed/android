package com.petfeed.petfeed.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.petfeed.petfeed.R
import com.petfeed.petfeed.adapter.DetailImagePagerAdapter
import com.petfeed.petfeed.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_detail_image.*

class DetailImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityUtils.statusBarSetting(window, this, R.color.white2, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)

        val images = ArrayList<String>().apply {
            addAll(intent.getStringArrayExtra("images"))
        }
        viewPager.adapter = DetailImagePagerAdapter(supportFragmentManager, images)
    }
}
