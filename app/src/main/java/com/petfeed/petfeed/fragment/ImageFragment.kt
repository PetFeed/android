package com.petfeed.petfeed.fragment


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.petfeed.petfeed.GlideApp

import com.petfeed.petfeed.R
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : Fragment() {

    var imageUrl = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        imageUrl = arguments!!.getString("imageUrl", "")

        GlideApp.with(this@ImageFragment)
                .load(imageUrl)
                .listener(requestListener)
                .into(imageView)
    }


    private val requestListener = object: RequestListener<Drawable>{
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            GlideApp.with(this@ImageFragment)
                    .load(imageUrl)
                    .listener(this)
                    .into(imageView)
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            return false
        }
    }
    companion object {
        fun newInstance(imageUrl: String) = ImageFragment().apply {
            arguments = Bundle().apply {
                putString("imageUrl", imageUrl)
            }
        }
    }
}
