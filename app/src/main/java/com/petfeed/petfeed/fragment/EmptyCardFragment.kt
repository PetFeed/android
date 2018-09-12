package com.petfeed.petfeed.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.petfeed.petfeed.R
import com.petfeed.petfeed.activity.RegisterCardActivity
import kotlinx.android.synthetic.main.fragment_empty_card.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.startActivity

class EmptyCardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_empty_card, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        container.onClick {
            startActivity<RegisterCardActivity>()
        }
    }

    companion object {
        fun newInstance() = EmptyCardFragment()
    }
}
