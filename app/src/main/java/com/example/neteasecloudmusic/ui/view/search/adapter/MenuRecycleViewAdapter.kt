package com.example.neteasecloudmusic.ui.view.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.api.HttpConfig
import com.example.neteasecloudmusic.ui.base.BaseRecyclerViewAdapter
import com.example.neteasecloudmusic.ui.base.BaseViewHolder
import com.example.neteasecloudmusic.ui.model.MenuBean

class MenuRecycleViewAdapter(): BaseRecyclerViewAdapter<MenuRecycleViewAdapter.MyViewHolder>() {
    private var menuOnClickListener: MenuRecycleViewAdapterOnClickListener? = null
    constructor(context: Context, dataList: ArrayList<*>): this() {
        this.context = context
        this.dataList = dataList
    }

    inner class MyViewHolder(itemView: View): BaseViewHolder(itemView) {
        fun setData(context: Context, view: View, menuBean: MenuBean) {
            view.findViewById<TextView>(R.id.menu_name).text = menuBean.menu_name
            val introduction = "${menuBean.musicNum}首,by${menuBean.username}"
            view.findViewById<TextView>(R.id.menu_introduction).text = introduction
            Glide.with(context)
                .load("${HttpConfig.sourceUrl}${menuBean.cover_image_path}")
                .into(view.findViewById(R.id.menu_image))
            menuOnClickListener?.tvOnClickListener(view.findViewById(R.id.menu_name), menuBean)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.directory_elem_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        context?.let {
            (holder as MyViewHolder).setData(
                it,
                holder.itemView,
                dataList?.get(position) as MenuBean
            )
        }
    }

    fun setData(dataList: ArrayList<MenuBean>) {
        this.dataList = dataList
        this.notifyDataSetChanged()
    }

    interface MenuRecycleViewAdapterOnClickListener {
        fun tvOnClickListener(view: View, menuBean: MenuBean)
    }

    fun setOnClick(menuOnClickListener: MenuRecycleViewAdapterOnClickListener) {
        this.menuOnClickListener = menuOnClickListener
    }
}