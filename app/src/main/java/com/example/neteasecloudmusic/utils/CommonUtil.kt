package com.example.neteasecloudmusic.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import java.io.Serializable
import java.net.URLEncoder

object CommonUtil{
    fun<T: Serializable?>getSerializable(activity: Activity, name:String, clazz: Class<T>):T{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent?.getSerializableExtra(name, clazz)!!
        else
            activity.intent?.getSerializableExtra(name)as T
    }
    fun encode(string: String): String = URLEncoder.encode(string, "UTF-8")
    fun toast(context: Context, tip: String) {
        Toast.makeText(context, tip, Toast.LENGTH_SHORT).show()
    }
}