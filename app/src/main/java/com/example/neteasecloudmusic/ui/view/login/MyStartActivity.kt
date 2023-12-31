package com.example.neteasecloudmusic.ui.view.login

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.neteasecloudmusic.databinding.ActivityStartBinding
import com.example.neteasecloudmusic.ui.base.BaseActivity
import com.example.neteasecloudmusic.ui.model.GlobalUserInfo
import com.example.neteasecloudmusic.ui.view.main.MainActivity
import com.example.neteasecloudmusic.ui.viewModel.UserViewModel
import com.example.neteasecloudmusic.utils.FileUtil

class MyStartActivity : BaseActivity() {
    private lateinit var binding: ActivityStartBinding
    private lateinit var userViewModel: UserViewModel

    override fun getLayout(): View {
        binding = ActivityStartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {}

    override fun initData() {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.userInfo.observe(this) {
            if (it != null) {
                GlobalUserInfo.setGlobalUserInfo(it)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }

        val emails = FileUtil.readFile(this, FileUtil.USEREMAIL)

        if (emails.isNotEmpty()) {
            userViewModel.getUserInfo(this, emails[0])
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    override fun initMusic(newIndex: Int) {
        // 这里不需要执行任何操作，留空即可
    }
}