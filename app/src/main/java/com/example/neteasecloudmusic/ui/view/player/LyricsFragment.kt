package com.example.neteasecloudmusic.ui.view.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.neteasecloudmusic.databinding.FragmentLyricsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class LyricsFragment : Fragment() {

    private var _binding: FragmentLyricsBinding? = null
    private val binding get() = _binding!!
    private val lyricParser = LyricParser()
    // 在 LyricsFragment 中声明一个 Handler
    private val handler = Handler(Looper.getMainLooper())
    private var lrcPath: String? = null
    private var hasLrcPathSet: Boolean = false
    private var isViewCreated = false
    private var loadJob: Job? = null
    private var isFragmentVisible = false
    private var songDuration: Long = 0 //保存歌曲时长
    private var currentPlayTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLyricsBinding.inflate(inflater, container, false)
        isFragmentVisible = true // 设置为可见
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isAdded && hasLrcPathSet) {
            // 当 Fragment 已添加到 Activity，并且歌词路径已设置时更新歌词
            updateLyrics()
        }
    }

    fun setLrcPath(lrcPath: String?) {
        this.lrcPath = lrcPath
        hasLrcPathSet = true
        Log.d("LyricsFragment", "Setting lrcPath: $lrcPath")
        updateLyrics()
    }

    fun updateLyrics() {
        // 清除之前的所有回调
        handler.removeCallbacksAndMessages(null)
        lifecycleScope.launch {
            if (isVisible && isAdded) {
                Log.d("LyricsFragment", "Updating lyrics. isVisible: $isVisible, isAdded: $isAdded")
                try {
                    if (hasLrcPathSet) {
                        Log.d("LyricsFragment", "Loading file from network: $lrcPath")
                        loadFileFromNetwork(lrcPath, object : LoadFileCallback {
                            override suspend fun onFileLoaded(inputStream: InputStream?) {
                                try {
                                    if (inputStream != null) {
                                        // 解析歌词文件
                                        lyricParser.setupLyricResource(inputStream, "UTF8")
                                        val lyricInfo = lyricParser.lyricInfo
                                        Log.d("LyricsFragment", "LyricInfo: $lyricInfo")
                                        // 添加检查，确保视图仍然有效
                                        if (isFragmentVisible) {
                                            // 将歌词信息传递给 LyricView
                                            binding.lyricView.setLyricInfo(lyricInfo)
                                        }
                                    } else {
                                        Log.e("LyricsFragment", "InputStream is null")
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    Log.e("LyricsFragment", "Error parsing lyrics: ${e.message}")
                                }
                            }

                            override suspend fun onFileLoadFailed(error: String) {
                                Log.e("LyricsFragment", "Failed to load network lyrics: $error")
                                // 添加检查，确保视图仍然有效
                                if (isFragmentVisible) {
                                    binding.tvLyrics.text = "暂无歌词"
                                }
                            }
                        })
                    } else {
                        Log.e("LyricsFragment", "lrcPath is null, cannot perform network request")
                    }
                } catch (e: Exception) {
                    Log.e("LyricsFragment", "Error in updateLyrics: ${e.message}")
                }
            }
        }
    }

    fun setCurrentPlayTime(time: Long) {
        currentPlayTime = time

        handler.post(object : Runnable {
            override fun run() {
                scrollToCurrentTime(currentPlayTime)
                handler.postDelayed(this, 100) // 每0.1秒更新一次
            }
        })
    }

    fun scrollToCurrentTime(currentPlayTime: Long) {
        val lyricInfo = lyricParser.lyricInfo
        lyricInfo.let { info ->
            for (i in 0 until info.songLines.size) {
                val lineInfo = info.songLines[i]
                if (currentPlayTime >= lineInfo.start) {
                    if (i < info.songLines.size - 1) {
                        val nextLineInfo = info.songLines[i + 1]
                        if (currentPlayTime < nextLineInfo.start) {
                            // 找到当前歌词行，可以通过你之前实现的滚动函数来滚动到该行
                            binding.lyricView.setCurrentPosition(i)

                            break
                        }
                    } else {
                        // 如果是最后一行，直接滚动到这一行
                        binding.lyricView.setCurrentPosition(i)
                        break
                    }
                }
            }
        }
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        // 获取LyricView实例
        val lyricView = binding.lyricView
        Log.d("LyricsFragment", "lrcPath in onViewCreated: $lrcPath")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isFragmentVisible = false // 设置为不可见
        // 取消异步加载任务
        handler.removeCallbacksAndMessages(null)
        // 取消异步加载任务
        loadJob?.cancel()
    }

    override fun onPause() {
        super.onPause()
        // 在这里停止歌曲播放，确保在Fragment不可见时停止播放
    }

    private fun getLyricsText(lyricInfo: LyricInfo?): String {
        val stringBuffer = StringBuilder()
        if (lyricInfo != null) {
            val iterator = lyricInfo.songLines.iterator()
            while (iterator.hasNext()) {
                val lineInfo = iterator.next()
                stringBuffer.append("${lineInfo.content}\n")
            }
        } else {
            Log.e("LyricsFragment", "LyricInfo is null")
        }
        Log.d("LyricsFragment", "歌词文本: $stringBuffer")
        return stringBuffer.toString()
    }

    interface LoadFileCallback {
        suspend fun onFileLoaded(inputStream: InputStream?)
        suspend fun onFileLoadFailed(error: String)
    }

    fun loadFileFromNetwork(url: String?, callback: LoadFileCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val urlObj = URL(url)
                val connection: HttpURLConnection =
                    withContext(Dispatchers.IO) {
                        urlObj.openConnection()
                    } as HttpURLConnection
                withContext(Dispatchers.IO) {
                    connection.connect()
                }

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = BufferedInputStream(connection.inputStream)
                    withContext(Dispatchers.Main) {
                        callback.onFileLoaded(inputStream)
                    }
                } else {
                    // 处理请求失败的情况
                    withContext(Dispatchers.Main) {
                        callback.onFileLoadFailed("请求失败的情况")
                    }
                }
            } catch (e: IOException) {
                // 处理网络异常
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback.onFileLoadFailed("网络异常")
                }
            }
        }
    }

    //设置歌曲时长
    fun setSongDuration(duration: Long) {
        songDuration = duration
        Log.d("滚动","动画时长：${songDuration}")
    }

}








