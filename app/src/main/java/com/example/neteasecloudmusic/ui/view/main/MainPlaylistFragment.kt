package com.example.neteasecloudmusic.ui.view.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.ui.model.MusicBean

class MainPlaylistFragment : DialogFragment(), MainPlaylistAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var playlistAdapter: MainPlaylistAdapter
    private lateinit var sharedViewModel: MainSharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_playlist, container, false)
        val musicList = arguments?.getSerializable("musicList") as ArrayList<MusicBean>?

        sharedViewModel = ViewModelProvider(requireActivity()).get(MainSharedViewModel::class.java)

        // 观察 SharedViewModel 的 LiveData
        sharedViewModel.currentMusicIndex.observe(viewLifecycleOwner, Observer {
            // 当音乐位置发生变化时，更新列表的选中状态
            playlistAdapter.setSelectedPosition(it)
        })

        recyclerView = view.findViewById(R.id.recyclerViewPlaylist)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(ItemDecoration(requireContext(), spacing = 8))

        playlistAdapter = MainPlaylistAdapter((musicList as? ArrayList<MusicBean>)?.toList() ?: emptyList(), this)
        recyclerView.adapter = playlistAdapter

        return view
    }
    override fun onItemClick(position: Int) {
        // 调用 MusicPlayerActivity 中的 handlePlaylistItemClick 方法
        (activity as? MainActivity)?.handlePlaylistItemClick(position)
    }
    override fun onResume() {
        super.onResume()

        val width = resources.displayMetrics.widthPixels  // 设置为屏幕宽度的80%
        val height = resources.displayMetrics.heightPixels * 0.6 // 设置为屏幕高度的50%

        dialog?.window?.setLayout(width.toInt(), height.toInt())
        dialog?.window?.setGravity(Gravity.BOTTOM) // 设置在底部显示
        animateDialog(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animateDialog(false)
    }

    private fun animateDialog(isOpen: Boolean) {
        val translationY = if (isOpen) 0f else (view?.height ?: 0).toFloat()

        ObjectAnimator.ofFloat(view, "translationY", translationY).apply {
            duration = 300 // 调整动画持续时间
            start()
        }
    }

    companion object {
        fun newInstance(musicList: ArrayList<MusicBean>): MainPlaylistFragment {
            val fragment = MainPlaylistFragment()
            val args = Bundle()
            args.putSerializable("musicList", musicList)
            fragment.arguments = args
            return fragment
        }
    }
    fun updateSelectedPosition(selectedPosition: Int) {
        playlistAdapter.setSelectedPosition(selectedPosition)
    }
}