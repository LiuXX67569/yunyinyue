package com.example.neteasecloudmusic.ui.view.main.fragment

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.ui.base.BaseFragment

class DiscoverFragment : BaseFragment() {

    override fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.discover_layout, container, false)
        //init animation
        val imgViAnima: ImageView = view.findViewById(R.id.discover_imgViAnima)
        val animationDrawable = imgViAnima.drawable as AnimationDrawable
        animationDrawable.start()
        return view
    }

    override fun setData(dataList: List<*>) {
        TODO("Not yet implemented")
    }
}