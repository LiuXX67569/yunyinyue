package com.example.neteasecloudmusic.ui.view.search.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.ui.base.BaseFragment
import com.example.neteasecloudmusic.ui.model.GlobalUserInfo
import com.example.neteasecloudmusic.ui.model.SingerBean
import com.example.neteasecloudmusic.ui.view.search.adapter.SingerRecycleViewAdapter
import com.example.neteasecloudmusic.ui.viewModel.SingerFragmentViewModel

class SingerFragment: BaseFragment() {
    private lateinit var context: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var singerRecycleViewAdapter: SingerRecycleViewAdapter
    private lateinit var singerFragmentViewModel: SingerFragmentViewModel
    private var singerBeanList: ArrayList<SingerBean> = ArrayList()

    override fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.singer_layout, container, false)
        context = view.context
        recyclerView = view.findViewById(R.id.singer_recycleView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        singerRecycleViewAdapter = SingerRecycleViewAdapter(context, singerBeanList)
        recyclerView.adapter = singerRecycleViewAdapter
        singerRecycleViewAdapter.setOnClick(object: SingerRecycleViewAdapter
            .SingerRecycleViewAdapterOnClickListener {
            override fun followOnClickListener(view: TextView, singerId: Int) {
                view.setOnClickListener {
                    view.text = if (GlobalUserInfo.followList.contains(singerId)) {
                        singerFragmentViewModel.notFollow()
                        getString(R.string.not_follow)
                    } else {
                        singerFragmentViewModel.follow()
                        getString(R.string.follow)
                    }
                }
            }
        })

        singerFragmentViewModel = ViewModelProvider(this)[SingerFragmentViewModel::class.java]
        singerFragmentViewModel.num.observe(this) {

        }
        return view
    }

    override fun setData(dataList: List<*>) {
        singerBeanList.clear()
        singerBeanList.addAll(dataList.filterIsInstance<SingerBean>() as ArrayList<SingerBean>)
        if (::singerRecycleViewAdapter.isInitialized) {
            singerRecycleViewAdapter = SingerRecycleViewAdapter(context, singerBeanList)
            recyclerView.adapter = singerRecycleViewAdapter
        }
    }
}