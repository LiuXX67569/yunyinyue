package com.example.neteasecloudmusic.ui.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.neteasecloudmusic.ui.view.player.LyricsFragment
import com.example.neteasecloudmusic.ui.view.player.RotationFragment


class MusicPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentMap = mutableMapOf<Int, Fragment>()

    override fun getItemCount(): Int {
        return 2 // 返回 ViewPager2 中的 Fragment 数量
    }

    override fun createFragment(position: Int): Fragment {
        // 如果已经创建过，直接返回缓存的实例
        fragmentMap[position]?.let { return it }

        // 创建新的 Fragment 实例
        val newFragment = when (position) {
            0 -> RotationFragment() // RotationFragment
            1 -> LyricsFragment() // LyricsFragment
            else -> throw IllegalArgumentException("Invalid position")
        }

        // 缓存新创建的实例
        fragmentMap[position] = newFragment

        return newFragment

    }

    fun getFragment(position: Int): Fragment? {
        return fragmentMap[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    fun getLyricsFragment(): LyricsFragment? {
        return fragmentMap[1] as? LyricsFragment
    }



}

