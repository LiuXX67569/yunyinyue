package com.example.neteasecloudmusic.ui.view.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.api.HttpConfig
import com.example.neteasecloudmusic.ui.base.BaseRecyclerViewAdapter
import com.example.neteasecloudmusic.ui.base.BaseViewHolder
import com.example.neteasecloudmusic.ui.model.GlobalUserInfo
import com.example.neteasecloudmusic.ui.model.SingerBean

class SingerRecycleViewAdapter(): BaseRecyclerViewAdapter<SingerRecycleViewAdapter.MyViewHolder>() {
    private var singerOnClickListener: SingerRecycleViewAdapterOnClickListener? = null
    constructor(context: Context, dataList: ArrayList<*>): this() {
        this.context = context
        this.dataList = dataList
    }

    inner class MyViewHolder(itemView: View): BaseViewHolder(itemView) {
        fun setData(context: Context, view: View, singerBean: SingerBean) {
            view.findViewById<TextView>(R.id.name).text = singerBean.singer_name
            val singerImage: ImageView =  view.findViewById(R.id.singer_image)
            Glide.with(context)
                .load("${HttpConfig.sourceUrl}${singerBean.photo_path}")
                .into(singerImage)
            view.findViewById<TextView>(R.id.tv_follow).text = if (GlobalUserInfo.followList.contains(singerBean.pk_id)) {
                context.getString(R.string.follow)
            } else {
                context.getString(R.string.not_follow)
            }
            singerOnClickListener?.followOnClickListener(view.findViewById(R.id.tv_follow), singerBean.pk_id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.singer_elem_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        context?.let {
            (holder as MyViewHolder).setData(
                it,
                holder.itemView,
                dataList?.get(position) as SingerBean
            )
        }
    }

    interface SingerRecycleViewAdapterOnClickListener {
        fun followOnClickListener(view: TextView, singerId: Int)
    }

    fun setOnClick(singerOnClickListener: SingerRecycleViewAdapterOnClickListener) {
        this.singerOnClickListener = singerOnClickListener
    }
}