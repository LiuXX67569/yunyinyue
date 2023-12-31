package com.example.neteasecloudmusic.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.neteasecloudmusic.R

class CustomBottomDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window: Window? = this.dialog?.window
        window?.decorView?.setPadding(0, 0, 0, 0)
        val lp: WindowManager.LayoutParams = window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        lp.windowAnimations = R.style.bottom_dialog_animation
        window.attributes = lp
        window.setBackgroundDrawable(object : ColorDrawable() {})
        return inflater.inflate(R.layout.bottom_dialog_layout, container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.bottom_dialog_bg_style)
    }
}