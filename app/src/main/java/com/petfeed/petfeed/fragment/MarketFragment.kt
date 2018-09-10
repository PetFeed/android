package com.petfeed.petfeed.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.*
import com.petfeed.petfeed.R
import com.petfeed.petfeed.activity.MainActivity
import com.petfeed.petfeed.util.ActivityUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_market.*
import org.jetbrains.anko.firstChild
import org.jetbrains.anko.sdk25.coroutines.onClick

class MarketFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_market, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setDrawer()

    }

    fun setDrawer() {
        val activity = context as MainActivity

        val toggle = ActionBarDrawerToggle(activity, activity.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toolbar.firstChild { true }.onClick {
            activity.drawerLayout.openDrawer(Gravity.START)
            ActivityUtils.statusBarSetting(activity.window, this@MarketFragment.context, R.color.white1, true)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        }


        activity.drawerLayout.addDrawerListener(
                object : DrawerLayout.DrawerListener {
                    override fun onDrawerStateChanged(p0: Int) {

                    }

                    override fun onDrawerSlide(p0: View, p1: Float) {
                    }

                    override fun onDrawerClosed(p0: View) {
                        ActivityUtils.statusBarSetting(activity.window, context, R.color.white1, false)
                        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    }

                    override fun onDrawerOpened(p0: View) {

                    }
                })
    }

    companion object {
        fun newInstance() = MarketFragment()
    }
}
