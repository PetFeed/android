package com.petfeed.petfeed.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.nitrico.lastadapter.LastAdapter
import com.petfeed.petfeed.BR

import com.petfeed.petfeed.R
import com.petfeed.petfeed.model.Notification
import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationFragment : Fragment() {

    val newNotificationList = ArrayList<Notification>().apply {
        add(Notification(isChecked = true))
        add(Notification())
    }
    val oldNotificationList = ArrayList<Notification>().apply {
        add(Notification(isChecked = true))
        add(Notification())
        add(Notification())
        add(Notification(isChecked = true))
        add(Notification())
    }
    val items = ArrayList<Any>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setRecyclerView()
    }

    fun setRecyclerView() {
        items.add("새 알림")
        items.addAll(newNotificationList)
        items.add("이전 알림")
        items.addAll(oldNotificationList)

        notification_recycler_view.layoutManager = LinearLayoutManager(context)
        LastAdapter(items, BR.item)
                .map<String>(R.layout.item_notification_title)
                .map<Notification>(R.layout.item_notification_content)
                .into(notification_recycler_view)
    }

    companion object {
        fun newInstance() = NotificationFragment()
    }
}
