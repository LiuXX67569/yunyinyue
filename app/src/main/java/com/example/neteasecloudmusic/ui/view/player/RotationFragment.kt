package com.example.neteasecloudmusic.ui.view.player

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.example.neteasecloudmusic.R

class RotationFragment : Fragment() {

    private lateinit var rotationView: RotationView
    // 不可变属性用于保存 RotationView
    private lateinit var savedRotationView: RotationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rotation, container, false)

        // 初始化 RotationView
        rotationView = view.findViewById(R.id.rotationView)
        // 监听布局变化
        rotationView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // 移除监听器，确保只调用一次
                rotationView.viewTreeObserver.removeOnPreDrawListener(this)
                // 初始化其他操作...
                initializeRotationView(rotationView)
                return true
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("RotationFragment", "RotationFragment onViewCreated")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存状态信息
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 恢复状态信息
    }



    // 初始化 rotationView 的函数
    fun initializeRotationView(view: RotationView) {
        savedRotationView = view
        Log.e("RotationFragment", "rotationView: $savedRotationView")


    }

    fun setCoverImagePath(path: String) {
        if (::savedRotationView.isInitialized) {
            savedRotationView.setCoverImagePath(path)
        } else {
            Log.e("RotationFragment", "rotationView 未被正确初始化。")
        }
    }

    fun setRotationDrawable(drawable: Drawable?) {
        if (::savedRotationView.isInitialized) {
            savedRotationView.setRotationDrawable(drawable)
        }
    }



    fun startRotation() {
        if (::savedRotationView.isInitialized) {
            savedRotationView.startRotation()
        } else {
            Log.e("RotationFragment", "rotationView 未被正确初始化。")
        }
    }

    fun pauseRotation() {
        if (::savedRotationView.isInitialized) {
            savedRotationView.pauseRotation()
        } else {
            Log.e("RotationFragment", "rotationView 未被正确初始化。")
        }
    }

    fun resumeRotation() {
        if (::savedRotationView.isInitialized) {
            savedRotationView.resumeRotation()
        } else {
            Log.e("RotationFragment", "rotationView 未被正确初始化。")
        }
    }


    fun getFlag():Boolean{
        return if (::savedRotationView.isInitialized) {
            savedRotationView.getFlag()
        } else {
            Log.e("RotationFragment", "rotationView 未被正确初始化。")
            false // 或者根据实际情况返回一个默认值
        }
    }

    fun setFlag() {
        if (::savedRotationView.isInitialized) {
            savedRotationView.setFlag()
        } else {
            Log.e("RotationFragment", "rotationView 未被正确初始化。")
        }
    }

  




}







