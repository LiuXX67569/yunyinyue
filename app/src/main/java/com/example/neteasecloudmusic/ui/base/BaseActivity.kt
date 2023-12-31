package com.example.neteasecloudmusic.ui.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.neteasecloudmusic.ui.view.main.MainActivity
import com.example.neteasecloudmusic.ui.view.main.MediaPlayerService
import com.example.neteasecloudmusic.ui.view.main.MusicDataHolder
import com.example.neteasecloudmusic.ui.view.main.MusicEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseActivity : AppCompatActivity() {
    private var dialogLoading: AlertDialog? = null
    private lateinit var mediaPlayerService: MediaPlayerService
    private var isServiceBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.MediaPlayerBinder
            mediaPlayerService = binder.getService()
            isServiceBound = true
            initData()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }
    fun getMediaPlayerService(): MediaPlayerService {
        return mediaPlayerService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        initView()
        // 注册 EventBus
        EventBus.getDefault().register(this)
        // 绑定到 MediaPlayerService
        val serviceIntent = Intent(this, MediaPlayerService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    // 添加 EventBus 订阅方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMusicEvent(event: MusicEvent) {
        initMusic(event.newIndex)
    }


    abstract fun getLayout(): View
    abstract fun initView()
    abstract fun initData()
    abstract fun initMusic(newIndex: Int)

    fun showLoading() {
        if (dialogLoading == null) {
            val builder = AlertDialog.Builder(this)
            val progressBar = ProgressBar(this)
            progressBar.isIndeterminate = true
            builder.setView(progressBar)
            builder.setCancelable(false)
            dialogLoading = builder.create()
        }
        dialogLoading!!.show()
    }

    fun dismissLoading() {
        dialogLoading?.dismiss()
        dialogLoading = null
    }
    override fun onDestroy() {
        super.onDestroy()
        // 注销 EventBus
        EventBus.getDefault().unregister(this)
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }


}