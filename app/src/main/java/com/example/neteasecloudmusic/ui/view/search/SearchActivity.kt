package com.example.neteasecloudmusic.ui.view.search

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.neteasecloudmusic.ui.view.adapter.ViewPaperAdapter
import com.example.neteasecloudmusic.databinding.SearchLayoutBinding
import com.example.neteasecloudmusic.ui.base.BaseActivity
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.ui.model.SingerBean
import com.example.neteasecloudmusic.ui.view.search.fragment.MenuFragment
import com.example.neteasecloudmusic.ui.view.search.fragment.MusicFragment
import com.example.neteasecloudmusic.ui.view.search.fragment.SearchAllFragment
import com.example.neteasecloudmusic.ui.view.search.fragment.SingerFragment
import com.example.neteasecloudmusic.ui.viewModel.SearchViewModel
import com.example.neteasecloudmusic.utils.CommonUtil
import com.google.android.material.tabs.TabLayoutMediator

class SearchActivity : BaseActivity() {
    private lateinit var binding: SearchLayoutBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchAllFragment: SearchAllFragment
    private lateinit var musicFragment: MusicFragment
    private lateinit var singerFragment: SingerFragment
    private lateinit var menuFragment: MenuFragment
    private var searchText: String = ""

    companion object {
        fun actionStart(context: Context, searchText: String) {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra("searchText", searchText)
            intent.putExtra("searchText", searchText)
            context.startActivity(intent)
        }
    }

    override fun getLayout(): View {
        binding = SearchLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        //init toolbar
        setSupportActionBar(binding.searchToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //init fragments
        searchAllFragment = SearchAllFragment()
        musicFragment = MusicFragment()
        menuFragment = MenuFragment()
        singerFragment = SingerFragment()
        val fragments = listOf(
            searchAllFragment,
            musicFragment,
            menuFragment,
            singerFragment)
        //init viewPaper
        val viewPaper = binding.searchViewPager
        val viewPaperAdapter = ViewPaperAdapter(this, fragments)
        viewPaper.adapter = viewPaperAdapter
        viewPaper.currentItem = 0
        //init tabLayout
        val tabLayout = binding.searchTabLayout
        val tabTitle = listOf("综合", "单曲", "歌单", "歌手")
        tabLayout.setSelectedTabIndicatorColor(Color.RED)
        TabLayoutMediator(tabLayout, viewPaper
        ) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()
        // 搜索
        searchText = intent.getStringExtra("searchText").toString()
        binding.searchText.setText(searchText)
        binding.searchTvSearch.setOnClickListener{
            searchText = binding.searchText.text.toString()
            getData()
        }
    }

    override fun initData() {
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        searchViewModel.musicList.observe(this) {
            musicFragment.setData(it)
            searchAllFragment.setMusicData(it)
        }
        searchViewModel.singerList.observe(this) {
            singerFragment.setData(it)
            searchAllFragment.setSingerData(it)
        }
        searchViewModel.menuList.observe(this) {
            menuFragment.setData(it)
            searchAllFragment.setMenuData(it)
        }
        searchViewModel.isShowLoading.observe(this) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
            }
        }
        searchViewModel.errorData.observe(this) {
            it.errMsg?.let { it1 -> CommonUtil.toast(this, it1) }
        }
        getData()
    }

    private fun getData() {
        searchViewModel.getMusicByName(CommonUtil.encode(searchText))
        searchViewModel.getSingerByName(CommonUtil.encode(searchText))
        searchViewModel.getMenuByMenuName(CommonUtil.encode(searchText))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }
    override fun initMusic(newIndex: Int) {
        // 这里不需要执行任何操作，留空即可
    }
}