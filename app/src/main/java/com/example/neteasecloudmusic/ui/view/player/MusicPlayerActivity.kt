package com.example.neteasecloudmusic.ui.view.player

import android.content.ContentValues.TAG
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.api.HttpConfig
import com.example.neteasecloudmusic.databinding.MusicplayerActivityLayoutBinding
import com.example.neteasecloudmusic.ui.base.BaseActivity
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.ui.view.adapter.MusicPagerAdapter
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder.currentMusicIndex
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder.isPlayingBeforeChange
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder.mediaPlayer
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder.musicList
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder.originalMusicList
import com.example.neteasecloudmusic.ui.viewModel.MusicPlayerViewModel
import com.example.neteasecloudmusic.utils.CommonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MusicPlayerActivity : BaseActivity() {
    private lateinit var binding: MusicplayerActivityLayoutBinding
    private lateinit var musicPlayerViewModel: MusicPlayerViewModel
    private var currentMusicIndex = 0//当前音乐
    private lateinit var seekBar: SeekBar//进度条
    private val handler = Handler(Looper.getMainLooper())
    //记录播放器切换歌曲前状态
    //private var isPlayingBeforeChange = false
    private lateinit var viewPager: ViewPager2
    private lateinit var musicPagerAdapter: MusicPagerAdapter
    //获取封面的fragment
    private lateinit var rotationFragment: RotationFragment
    //获取歌词的fragment
    private lateinit var lyricsFragment: LyricsFragment
    //获取播放列表fragment
    private lateinit var playlistFragment: PlaylistFragment
    //获取播放列表共享数据模型
    private lateinit var sharedViewModel: SharedViewModel
    // 播放模式，0表示顺序播放，1表示随机播放
    private var playMode = 0

    override fun getLayout(): View {
        binding = MusicplayerActivityLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        rotationFragment = RotationFragment()
        lyricsFragment = LyricsFragment()
        // 初始化 MusicPagerAdapter
        musicPagerAdapter = MusicPagerAdapter(supportFragmentManager, lifecycle)
        // 获取 ViewPager2 的引用
        viewPager = binding.viewPager
        viewPager.adapter = musicPagerAdapter
        // 设置ViewPager的预加载页面数为1
        viewPager.offscreenPageLimit = 1
        viewPager.setCurrentItem(0, false) // 将初始片段设置为 RotationFragment
        // ViewPager2 页面切换监听器
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // 通过 ViewPager2 获取当前的 Fragment
                val currentFragment = supportFragmentManager.findFragmentByTag("f$position")
                // 初始化 Fragment
                if (currentFragment is RotationFragment) {
                    rotationFragment = currentFragment
                }else if (currentFragment is LyricsFragment) {
                    lyricsFragment = currentFragment
                }
                //设置歌词界面的显示和隐藏
                val tag = "android:switcher:" + viewPager.id + ":" + position
                val oldFragment = supportFragmentManager.findFragmentByTag(tag)
                if (oldFragment != null) {
                    supportFragmentManager.beginTransaction()
                        .add(viewPager.id, rotationFragment, "f0")
                        .add(viewPager.id, lyricsFragment, "f1")
                        .hide(oldFragment)
                        .show(currentFragment!!)
                        .commit()
                }
            }
        })
        //播放列表赋值
        playlistFragment = PlaylistFragment.newInstance(musicList)
        //共享数据赋值
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        seekBar = findViewById(R.id.seekBar)//进度条
        //更新UI
        Log.d("音乐播放器页面","是否播放：${mediaPlayer.isPlaying}")
        if (mediaPlayer.isPlaying) {
            rotationFragment.startRotation()
            binding.IBplay.setImageResource(R.drawable.img_playerstop)
        } else {
            rotationFragment.pauseRotation()
            binding.IBplay.setImageResource(R.drawable.img_playerplay)
        }
        //点击事件
        binding.IBplay.setOnClickListener {//播放按钮
            handlePlayButtonClick()
        }
        binding.IBnext.setOnClickListener {//下一首
            if (musicList.size > 0) {
                changeMusic(1)
            }
        }
        binding.IBlast.setOnClickListener {//上一首
            if (musicList.size > 0) {
                changeMusic(-1)
            }
        }
        binding.IBreturn.setOnClickListener {//返回主界面
            onBackPressed()
        }
        binding.IBlist.setOnClickListener {//打开播放列表
            showPlaylistFragment()
        }
        binding.IBplaymode.setOnClickListener{//切换播放模式
            togglePlayMode()
        }

        // 设置SeekBar监听器,进度条
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 用户拖动SeekBar时触发
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 用户开始拖动SeekBar时触发
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 用户停止拖动SeekBar时触发
            }
        })
        // 更新SeekBar的进度
        handler.post(object : Runnable {
            override fun run() {
                if (!isFinishing) {
                    runOnUiThread {
                        if (mediaPlayer.isPlaying) {
                            seekBar.progress = mediaPlayer.currentPosition
                        }
                    }
                    handler.postDelayed(this, 1000) // 每秒更新一次
                }
            }
        })
    }

    // 公共函数处理 IBplay 按钮点击事件
    fun handlePlayButtonClick() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            rotationFragment.pauseRotation()
            binding.IBplay.setImageResource(R.drawable.img_playerplay)
        } else {
            mediaPlayer.start()
            if (rotationFragment.getFlag()) {
                rotationFragment.resumeRotation()
            } else {
                rotationFragment.startRotation()
            }
            binding.IBplay.setImageResource(R.drawable.img_playerstop)
        }
    }

    override fun initData() {
        currentMusicIndex = MusicDataHolder.currentMusicIndex.value!!
        Log.d("列表位置检查","initData播放器列表位置：${currentMusicIndex}")
        Log.d("列表位置检查","initData静态列表位置：${MusicDataHolder.currentMusicIndex.value}")

        initMusic(currentMusicIndex)
        musicPlayerViewModel = ViewModelProvider(this)[MusicPlayerViewModel::class.java]
        musicPlayerViewModel.musicList.observe(this) {
            if (it.isNotEmpty()) {
                musicList.clear()
                musicList.addAll(it)
            }
        }
        musicPlayerViewModel.isShowLoading.observe(this) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
            }
        }
        musicPlayerViewModel.errorData.observe(this) {
            it.errMsg?.let { it1 -> CommonUtil.toast(this, it1) }
        }
        musicPlayerViewModel.errorData.observe(this) { errorData ->
            errorData.errMsg?.let { CommonUtil.toast(this, it) }
        }
    }

    //打开播放列表界面
    private fun showPlaylistFragment() {
        playlistFragment.show(supportFragmentManager, "playlist_dialog")
    }
    //切换播放模式
    private fun togglePlayMode() {
        try {
            playMode = if (playMode == 0) 1 else 0

            isPlayingBeforeChange = mediaPlayer.isPlaying
            // 先停止MediaPlayer
            stopMediaPlayer()

            if (mediaPlayer.isPlaying) {
                mediaPlayer.start()
                rotationFragment.startRotation()
                binding.IBplay.setImageResource(R.drawable.img_playerstop)
            } else {
                rotationFragment.pauseRotation()
                binding.IBplay.setImageResource(R.drawable.img_playerplay)
            }
            binding.IBplay
            // 更新音乐列表的顺序
            updateMusicListOrder()
            // 根据新的播放模式，重新初始化音乐
            initMusic(currentMusicIndex)
            // 设置 IBplayMode 的图标
            setPlayModeIcon()
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun updateMusicListOrder() {
        when (playMode) {
            // 顺序播放，还原为原始顺序
            0 -> {
                musicList.clear()
                musicList.addAll(originalMusicList)
            }
            // 随机播放，打乱音乐列表的顺序
            1 -> {
                musicList.shuffle()//随机排列
            }
        }
    }
    private fun setPlayModeIcon() {
        try {
            val iconResource = when (playMode) {
                0 -> R.drawable.icon_order // 顺序播放
                1 -> R.drawable.icon_random // 随机播放
                else -> R.drawable.icon_order // 默认为顺序播放
            }

            binding.IBplaymode.setImageResource(iconResource)
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun stopMediaPlayer() {
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun initMusic(index: Int) {

        currentMusicIndex = index
        Log.d("列表位置检查","initMusic播放器列表位置：${currentMusicIndex}")
        Log.d("列表位置检查","initMusic静态列表位置：${index}")

        mediaPlayer.setOnErrorListener { _, what, extra ->
            // 处理MediaPlayer的错误，添加详细的逻辑
            Log.e(TAG, "MediaPlayer error: $what, $extra")
            // 根据错误类型进行处理
            when (what) {
                MediaPlayer.MEDIA_ERROR_IO -> {
                    // 处理IO错误
                    // 例如，尝试重新加载音乐或显示用户提示
                }
                MediaPlayer.MEDIA_ERROR_MALFORMED -> {
                    // 处理媒体数据错误
                    // 例如，尝试重新加载音乐或显示用户提示
                }
                // 其他错误类型...
            }
            // 返回true表示错误被处理
            return@setOnErrorListener true
        }
        mediaPlayer.setOnPreparedListener {
            seekBar.max = mediaPlayer.duration
            var musicDuration:Long = mediaPlayer.duration.toLong()
            if (isPlayingBeforeChange) {
                mediaPlayer.start()
                rotationFragment.startRotation()
                binding.IBplay.setImageResource(R.drawable.img_playerstop)
            } else {
                rotationFragment.pauseRotation()
                rotationFragment.setFlag()
                binding.IBplay.setImageResource(R.drawable.img_playerplay)
            }
            //mediaPlayer.prepareAsync()

            // 将歌曲时长传递给 LyricsFragment
            lifecycleScope.launch {
                val lyricsFragment = (viewPager.adapter as? MusicPagerAdapter)?.getLyricsFragment()
                lyricsFragment?.setSongDuration(musicDuration)
                Log.d("Lyrics", "传递时长：$musicDuration")

                while (isActive) {
                    // 在 IO 线程中执行耗时操作，比如获取 mediaPlayer.currentPosition
                    val currentPlayTime = withContext(Dispatchers.IO) {
                        mediaPlayer.currentPosition.toLong()
                    }
                    // 在主线程更新 UI
                    withContext(Dispatchers.Main) {
                        lyricsFragment?.setCurrentPlayTime(currentPlayTime)
                    }
                    // 添加适当的延时，避免过于频繁地执行
                    delay(100) // 每0.1秒更新一次
                }

            }

        }

        //封面图片文件
        Glide.with(this@MusicPlayerActivity)
            .load("${HttpConfig.servlet}${musicList[index].cover_image_path}")
            .into(object: SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    rotationFragment.setRotationDrawable(resource)
                    rotationFragment.setCoverImagePath("${HttpConfig.servlet}${musicList[index].cover_image_path}")
                }
            })
        binding.tvSongTitle.text = musicList[index].music_name
        binding.tvArtist.text = musicList[index].singer

        //歌词文件
        val lrcUrl = "${HttpConfig.servlet}${musicList[index].lyrics_path}"
        Log.d("MusicPlayerActivity","歌词链接：${lrcUrl}")
        lifecycleScope.launch {
            val lyricsFragment = (viewPager.adapter as? MusicPagerAdapter)?.getLyricsFragment()
            lyricsFragment?.setLrcPath(lrcUrl)
        }
        // 更新当前音乐位置到 SharedViewModel
        sharedViewModel.currentMusicIndex.value = index
        // 共享当前的音乐列表到 SharedViewModel
        sharedViewModel.updateMusicList(musicList)
    }
    private fun changeMusic(offset: Int) {
        try{
            // 记录当前播放器状态
            isPlayingBeforeChange = mediaPlayer.isPlaying
            currentMusicIndex = (currentMusicIndex + offset + musicList.size) % musicList.size
            MusicDataHolder.updateCurrentMusicIndex(currentMusicIndex)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.start()
                rotationFragment.startRotation()
                binding.IBplay.setImageResource(R.drawable.img_playerstop)
            } else {
                rotationFragment.pauseRotation()
                binding.IBplay.setImageResource(R.drawable.img_playerplay)
            }
            // 获取 LyricsFragment 实例并更新歌词
            val lyricsFragment = supportFragmentManager.findFragmentByTag("f1") as? LyricsFragment
            lyricsFragment?.setLrcPath("${HttpConfig.servlet}${musicList[currentMusicIndex].lyrics_path}")
            // 更新当前音乐位置到 SharedViewModel
            sharedViewModel.currentMusicIndex.value = currentMusicIndex
        }catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun handlePlaylistItemClick(selectedPosition: Int) {
        // 处理选中音乐的逻辑
        if (selectedPosition in musicList.indices) {
            isPlayingBeforeChange = mediaPlayer.isPlaying
            // 更新当前音乐的位置
            currentMusicIndex = selectedPosition
            MusicDataHolder.updateCurrentMusicIndex(currentMusicIndex)
            // 调用 PlaylistFragment 中的函数来更新选中位置
            playlistFragment.updateSelectedPosition(currentMusicIndex)
            // 模仿 changeMusic 函数进行切换音乐
            if (mediaPlayer.isPlaying) {
                mediaPlayer.start()
                rotationFragment.startRotation()
                binding.IBplay.setImageResource(R.drawable.img_playerstop)
            } else {
                rotationFragment.pauseRotation()
                binding.IBplay.setImageResource(R.drawable.img_playerplay)
            }
            // 获取 LyricsFragment 实例并更新歌词
            val lyricsFragment = supportFragmentManager.findFragmentByTag("f1") as? LyricsFragment
            lyricsFragment?.setLrcPath("${HttpConfig.servlet}${musicList[currentMusicIndex].lyrics_path}")
        }
        sharedViewModel.currentMusicIndex.value = selectedPosition
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // 停止Handler的回调
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}

