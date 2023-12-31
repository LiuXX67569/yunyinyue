package com.example.neteasecloudmusic.ui.view.player

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.example.neteasecloudmusic.ui.view.adapter.MusicPagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.CopyOnWriteArrayList


data class LineInfo(val content: String, val start: Long)

data class LyricInfo(
    val songLines: CopyOnWriteArrayList<LineInfo>,
    var songArtist: String = "",
    var songTitle: String = "",
    var songAlbum: String = "",
    var songOffset: Long = 0,
    var songVersion: String = ""
)



class LyricParser {

    internal val lyricInfo = LyricInfo(CopyOnWriteArrayList())

    private lateinit var viewPager: ViewPager2
    private lateinit var musicPagerAdapter: MusicPagerAdapter
    //获取歌词的fragment
    private lateinit var lyricsFragment: LyricsFragment
    private var flag_refresh = false
    private var flag_position = 0
    private val progressChangedListener: OnProgressChangedListener? = null
    private val normalColor: Int = Color.parseColor("#FFFFFF")
    private val selectedColor = Color.parseColor("#07FA81")


    internal class DataHolder {
        var builder: SpannableStringBuilder? = null
        var lines: List<LineInfo>? = null
        var refresh = false
        var position = 0
    }

    fun getInstance(context: Context?): LyricParser? {
        return LyricParser()
    }

    suspend fun setupLyricResource(inputStream: InputStream, charsetName: String) {
        withContext(Dispatchers.IO) {
            try {
                val inputStreamReader = InputStreamReader(inputStream, charsetName)
                val reader = BufferedReader(inputStreamReader)
                var line: String?
                // 创建一个新的 LyricInfo 对象
                val newLyricInfo = LyricInfo(CopyOnWriteArrayList())

                while (reader.readLine().also { line = it } != null) {
                    Log.d("LyricParser", "Line from file: $line")
                    analyzeLyric(newLyricInfo, line) // 使用LyricParser的成员变量
                }
                reader.close()
                inputStream.close()
                inputStreamReader.close()

                // 将新的 LyricInfo 赋值给成员变量
                // 将新的 LyricInfo 赋值给成员变量
                lyricInfo.songLines.clear()
                lyricInfo.songLines.addAll(newLyricInfo.songLines)
                lyricInfo.songArtist = newLyricInfo.songArtist
                lyricInfo.songTitle = newLyricInfo.songTitle
                lyricInfo.songAlbum = newLyricInfo.songAlbum
                lyricInfo.songOffset = newLyricInfo.songOffset
                lyricInfo.songVersion = newLyricInfo.songVersion

                // 通过打印解析的内容进行验证
                printParsedLyrics(lyricInfo)
            } catch (e: IOException) {
                Log.e("LyricParser", "Error setting up lyric resource: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    private fun analyzeLyric(lyricInfo: LyricInfo, line: String?) {
        Log.d("LyricParser", "Analyzing line: $line")
        line?.let {
            val index = it.lastIndexOf("]")
            val timestampLength = index - 1 // 计算时间戳的长度
            Log.d("LyricParser", "时间戳长度: $timestampLength")
            when {
                it.startsWith("[offset:") -> {
                    val string = it.substring(8, index).trim()
                    lyricInfo.songOffset = string.toLong()
                }
                it.startsWith("[ti:") -> {
                    val string = it.substring(4, index).trim()
                    lyricInfo.songTitle = string
                }
                it.startsWith("[ar:") -> {
                    val string = it.substring(4, index).trim()
                    lyricInfo.songArtist = string
                }
                it.startsWith("[al:") -> {
                    val string = it.substring(4, index).trim()
                    lyricInfo.songAlbum = string
                }
                it.startsWith("[by:") -> {
                    // 跳过
                }
                it.startsWith("[ver:") -> {
                    // 处理版本信息
                    val endIndex = it.indexOf("]")
                    if (endIndex != -1) {
                        val versionString = it.substring(5, endIndex).trim()
                        lyricInfo.songVersion = versionString
                    } else {
                        // 处理版本信息字符串不完整的情况
                        Log.w("LyricParser", "Incomplete version information: $it")
                    }
                }

                timestampLength == 8 && it.trim().length > 10 -> {
                    val lineInfo = LineInfo(it.substring(10, it.length), measureStartTimeMillis(it.substring(0, 10)))
                    lyricInfo.songLines.add(lineInfo)
                }
                timestampLength == 9 && it.trim().length > 11 -> {
                    val lineInfo = LineInfo(it.substring(11, it.length), measureStartTimeMillis(it.substring(0, 11)))
                    lyricInfo.songLines.add(lineInfo)
                }

                else -> {

                    Log.w("LyricParser", "Unrecognized line: $line")
                }

            }
        }
    }

    private fun measureStartTimeMillis(str: String): Long {
        return when (str.length) {
            10 -> {
                // 处理 [00:00.07] 这种情况
                val minute = str.substring(1, 3).toLong()
                val second = str.substring(4, 6).toLong()
                val millisecond = str.substring(7, 9).toLong()
                millisecond + second * 1000 + minute * 60 * 1000
            }
            11 -> {
                // 处理 [00:00.000] 这种情况
                val minute = str.substring(1, 3).toLong()
                val second = str.substring(4, 6).toLong()
                val millisecond = str.substring(7, 10).toLong()
                millisecond + second * 1000 + minute * 60 * 1000
            }
            else -> {
                // 其他情况，可以根据需要进行处理
                Log.e("LyricParser", "Invalid time format: $str")
                0
            }
        }
    }



    private fun printParsedLyrics(lyricInfo: LyricInfo) {
        val songLinesCopy = ArrayList(lyricInfo.songLines.toList())
        val stringBuffer = StringBuilder()
        songLinesCopy.forEach { lineInfo ->
            stringBuffer.append("${lineInfo.content}\n")
        }
        println(stringBuffer.toString())
        // 添加日志
        Log.d("LyricParser", "Parsed lyrics:\n$stringBuffer")
    }

    fun setCurrentTimeMillis(timeMillis: Long) {
        // 处理当前播放时间对应的歌词逻辑
        lyricInfo.setCurrentTimeMillis(timeMillis)
    }

    fun LyricInfo.setCurrentTimeMillis(timeMillis: Long) {
        val lines = this.songLines
        if (lines != null) {
            GlobalScope.launch {
                var position = 0
                val stringBuilder = SpannableStringBuilder()

                for (i in 0 until lines.size) {
                    if (lines[i].start < timeMillis) {
                        position = i
                    } else {
                        break
                    }
                }

                if (position == flag_position && !flag_refresh) {
                    return@launch
                }

                flag_position = position

                for (i in 0 until lines.size) {
                    val span: CharacterStyle = if (i != position) {
                        ForegroundColorSpan(normalColor)
                    } else {
                        ForegroundColorSpan(selectedColor)
                    }

                    val line = lines[i].content
                    val spannableString = SpannableString("$line\n")
                    spannableString.setSpan(span, 0, spannableString.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    stringBuilder.append(spannableString)
                }

                val message = Message()
                message.what = 0x159
                val dataHolder = DataHolder()
                dataHolder.builder = stringBuilder
                dataHolder.position = position
                dataHolder.refresh = flag_refresh
                dataHolder.lines = lines
                message.obj = dataHolder

                handler.sendMessage(message)

                if (flag_refresh) {
                    flag_refresh = false
                }
            }
        } else {
            val message = Message()
            message.what = 0x159
            val dataHolder = DataHolder()
            dataHolder.builder = null
            dataHolder.position = -1
            message.obj = dataHolder

            handler.sendMessage(message)
        }
    }

    private var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val `object` = msg.obj
            if (`object` != null && `object` is DataHolder) {
                when (msg.what) {
                    0x159 -> if (null != progressChangedListener) {
                        progressChangedListener.onProgressChanged(
                            `object`.builder,
                            `object`.position,
                            `object`.refresh
                        )
                        if (`object`.lines != null) {
                            progressChangedListener.onProgressChanged(
                                `object`.lines!![`object`.position].content,
                                `object`.refresh
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }



    interface OnProgressChangedListener {
        fun onProgressChanged(singleLine: String?, refresh: Boolean)
        fun onProgressChanged(
            stringBuilder: SpannableStringBuilder?,
            lineNumber: Int,
            refresh: Boolean
        )
    }

}