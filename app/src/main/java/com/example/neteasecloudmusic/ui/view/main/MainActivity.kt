package com.example.neteasecloudmusic.ui.view.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.databinding.MainLayoutBinding
import com.example.neteasecloudmusic.ui.base.BaseActivity
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.ui.view.adapter.ViewPaperAdapter
import com.example.neteasecloudmusic.ui.view.main.fragment.DiscoverFragment
import com.example.neteasecloudmusic.ui.view.main.fragment.MineFragment
import com.example.neteasecloudmusic.ui.view.player.MusicPlayerActivity
import com.example.neteasecloudmusic.ui.view.player.SharedViewModel
import com.example.neteasecloudmusic.ui.view.search.SearchActivity
import com.example.neteasecloudmusic.ui.viewModel.MusicPlayerViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.example.neteasecloudmusic.api.HttpConfig
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder.currentMusicIndex
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder.mediaPlayer
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder.musicList
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder.originalMusicList

class MainActivity : BaseActivity() {
    private lateinit var binding: MainLayoutBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var discoverFragment: DiscoverFragment
    private lateinit var mineFragment: MineFragment
    private lateinit var musicPlayerViewModel: MusicPlayerViewModel//共享音乐数据
    private var currentMusicIndex: Int = 0 // 当前歌曲位置，默认为0

    override fun getLayout(): View {
        binding = MainLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.img_extra_cross)
        }
        //init drawerLayout
        drawerLayout = binding.mainDrawerLayout
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.mainNavView.let { it ->
            it.setCheckedItem(R.id.nav_about)
            it.setNavigationItemSelectedListener {
                drawerLayout.closeDrawers()
                when (it.itemId) {
                    R.id.nav_about -> {
                        Toast.makeText(this, "关于我们", Toast.LENGTH_SHORT).show()
                    }
                    R.id.nav_exit -> {
                        Snackbar.make(drawerLayout, "继续退出", Snackbar.LENGTH_SHORT)
                            .setAction("Do it!") {
                                Toast.makeText(this, "滚！", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .setAction("Undo") {
                                Toast.makeText(this, "取消退出", Toast.LENGTH_SHORT)
                                    .show()
                            }.show()
                    }
                    R.id.header_imgViPortrait -> {
                        Toast.makeText(this, "我的界面", Toast.LENGTH_SHORT).show()
                    }
                    R.id.header_tvUsername -> {
                        Toast.makeText(this, "我的界面", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
        //init fragments
        discoverFragment = DiscoverFragment()
        mineFragment = MineFragment()
        val fragments = listOf(discoverFragment, mineFragment)
        //init viewPaper
        val viewPaper = binding.mainViewPager
        val viewPaperAdapter = ViewPaperAdapter(this, fragments)
        viewPaper.adapter = viewPaperAdapter
        viewPaper.currentItem = 0
        //init tabLayout
        val tabLayout = binding.mainTabLayout
        val tabTitle = listOf("发现", "我的")
        val tabIcon = listOf(R.drawable.img_disc, R.drawable.img_me)
        TabLayoutMediator(tabLayout, viewPaper
        ) { tab, position ->
            tab.text = tabTitle[position]
            tab.setIcon(tabIcon[position])
        }.attach()
        //main
        binding.mainTvSearch.setOnClickListener {
            val searchText = binding.searchText.text.toString()
            binding.searchText.text.clear()
            SearchActivity.actionStart(this, searchText)
        }

        //主页播放器
        // 初始化媒体播放器
        //打开播放器界面
        binding.TRmusicplayer.setOnClickListener {
            val intent = Intent(this, MusicPlayerActivity::class.java)//打开MusicPlayerActivity
            startActivity(intent)
        }
        //点击Home界面播放按钮
        binding.IBmainplay.setOnClickListener{
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.IBmainplay.setImageResource(R.drawable.img_play)
            } else {
                mediaPlayer.start()
                binding.IBmainplay.setImageResource(R.drawable.img_stop)
            }
        }
        binding.IBmainplaylist.setOnClickListener {

        }

    }

    override fun initData() {
        // 获取 MediaPlayerService 的实例
        val mediaPlayerServiceInstance = getMediaPlayerService()
        mediaPlayer = mediaPlayerServiceInstance.getMediaPlayer()//获取播放器服务，并共享媒体播放器
        musicPlayerViewModel = ViewModelProvider(this)[MusicPlayerViewModel::class.java]
        musicPlayerViewModel.getAllMusic()//获取所有音乐
        //更新当前页面音乐列表
        musicPlayerViewModel.musicList.observe(this) {newList ->
            if (newList.isNotEmpty()) {
                musicList.clear()
                musicList.addAll(newList)
                //保存原始顺序列表副本
                originalMusicList.clear()
                originalMusicList.addAll(newList)
                //更新当前位置并初始化第一首歌曲
                MusicDataHolder.updateCurrentMusicIndex(currentMusicIndex)
                Log.d("列表位置检查","initdata主页列表位置：${currentMusicIndex}")
                Log.d("列表位置检查","initdata静态列表位置：${MusicDataHolder.currentMusicIndex.value}")
            }
        }

    }

    // 初始化当前歌曲
    override fun initMusic(index: Int) {
        currentMusicIndex = MusicDataHolder.currentMusicIndex.value!!
        Log.d("列表位置检查","initMusic主页列表位置：${currentMusicIndex}")
        Log.d("列表位置检查","initMusic静态列表位置：${MusicDataHolder.currentMusicIndex.value}")
        //初始化mp3
        mediaPlayer.reset()
        val musicUrl = "${HttpConfig.servlet}${musicList[index].mp3_file_path}"
        val uri = Uri.parse(musicUrl)
        mediaPlayer.setDataSource(applicationContext, uri)
        mediaPlayer.prepareAsync()

        //封面图片文件
        Glide.with(this@MainActivity)
            .load("${HttpConfig.servlet}${musicList[index].cover_image_path}")
            .into(object: SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    binding.IMGcover.setImageDrawable(resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })

        binding.TVSongTitle.text = musicList[index].music_name
        binding.TVArtist.text = musicList[index].singer


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}