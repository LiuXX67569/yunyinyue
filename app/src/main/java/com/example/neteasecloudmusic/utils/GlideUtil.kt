package com.example.neteasecloudmusic.utils

import android.content.Context
import android.os.Looper
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.request.RequestOptions
import com.example.neteasecloudmusic.R
import java.io.File
import kotlin.concurrent.thread

object GlideUtil {

    // 关闭缓存
    val requestOptions: RequestOptions by lazy {
        RequestOptions()
            .error(R.drawable.img_portrait)
            .circleCrop()   // 圆形图案
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
    }

    // 清除图片磁盘缓存
    fun clearImageDiskCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                thread {
                    Glide.get(context).clearDiskCache()
                }
            } else {
                Glide.get(context).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 清除图片缓存
    fun clearImageMemoryCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // 清除图片所有缓存
    fun clearImageAllCache(context: Context) {
        clearImageDiskCache(context)
        clearImageMemoryCache(context)
        val imageExternalCatchDir: String = context.externalCacheDir!!.path + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
        deleteFolderFile(imageExternalCatchDir, true)
    }

    private fun deleteFolderFile(filePath: String, deleteThisPath: Boolean = false): Boolean {
        if (!TextUtils.isEmpty(filePath)) {
            val file = File(filePath)
            if (file.isDirectory) {
                val files = file.listFiles()
                for (f in files) {
                    deleteFolderFile(f.absolutePath, true)
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory) {
                    file.delete()
                } else {
                    if (file.listFiles().isEmpty()) {
                        file.delete()
                    }
                }
            }
            return true
        }
        return false
    }

}