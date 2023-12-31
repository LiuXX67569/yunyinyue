package com.example.neteasecloudmusic.ui.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPaperAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private lateinit var fragments: List<Fragment>

    constructor(fragmentActivity: FragmentActivity, fragments: List<Fragment>) :
            this(fragmentActivity) {
            this.fragments = fragments
    }
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun getItemId(position: Int): Long {
        return fragments[position].hashCode().toLong()
    }
}