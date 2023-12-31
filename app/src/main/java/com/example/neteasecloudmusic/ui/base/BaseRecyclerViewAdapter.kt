package com.example.neteasecloudmusic.ui.base

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T: BaseViewHolder>()
    : RecyclerView.Adapter<BaseViewHolder>(){
    var context: Context? = null
    var dataList: ArrayList<*>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(null)
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

}