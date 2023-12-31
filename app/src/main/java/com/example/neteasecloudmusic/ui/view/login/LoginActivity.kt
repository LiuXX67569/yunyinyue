package com.example.neteasecloudmusic.ui.view.login

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.neteasecloudmusic.databinding.LoginLayoutBinding
import com.example.neteasecloudmusic.ui.base.BaseActivity
import com.example.neteasecloudmusic.ui.model.GlobalUserInfo
import com.example.neteasecloudmusic.ui.model.UserInfoBean
import com.example.neteasecloudmusic.ui.view.main.MainActivity
import com.example.neteasecloudmusic.ui.viewModel.LoginViewModel
import com.example.neteasecloudmusic.utils.CommonUtil
import com.example.neteasecloudmusic.utils.DatabaseUtil
import com.example.neteasecloudmusic.utils.FileUtil
import kotlin.concurrent.thread

class LoginActivity : BaseActivity() {
    private lateinit var binding: LoginLayoutBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun getLayout(): View {
        binding = LoginLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        binding.loginTvEnterNow.setOnClickListener {
            val userInfoBean = UserInfoBean()
            userInfoBean.pk_id = -1
            userInfoBean.username = "游客"
            userInfoBean.portrait = "userInfo/images/portrait_default.png"
            GlobalUserInfo.setGlobalUserInfo(userInfoBean)
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnGetCode.setOnClickListener {
            loginViewModel.requestCode(binding.loginEdtTxtEmail.text.toString())
        }

        binding.loginBtnLogin.setOnClickListener {
            val email = binding.loginEdtTxtEmail.text.toString()
            val code = binding.loginEdtTxtCode.text.toString()
            loginViewModel.verifyCode(email, code)
        }
    }

    override fun initData() {
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel.time.observe(this) {
            if (it > 0) {
                binding.btnGetCode.text = "${it}秒后可以再次获取"
            } else {
                binding.btnGetCode.text = "获取验证码"
                binding.btnGetCode.isClickable = true
            }
        }
        loginViewModel.verifyStatus.observe(this) {
            AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(it.msg)
                .setCancelable(true)
                .create()
                .show()
            if (it.status != -1) {
                loginViewModel.onTime()
            } else {
                binding.btnGetCode.isClickable = true
            }
        }
        loginViewModel.userInfo.observe(this) {
            val dialog = AlertDialog.Builder(this)
                .setTitle(with(StringBuilder()) {
                    if (it.pk_id > 0) {
                        append("登录成功")
                    } else {
                        append("提示")
                    }
                    toString()
                })
                .setMessage(with(StringBuilder()) {
                    if (it.pk_id >= 0) {
                        append("欢迎体验本应用")
                    } else {
                        append("验证码错误")
                    }
                    toString()
                })
                .setCancelable(true)
                .create()
            dialog.show()
            if (it.pk_id >= 0) {
                GlobalUserInfo.setGlobalUserInfo(it)
                FileUtil.saveFile(this, FileUtil.USEREMAIL, it.email)
                thread {
                    if (DatabaseUtil(this).getUserInfo(it.email) == UserInfoBean() ||
                        DatabaseUtil(this).getUserInfo(it.email) == null) {
                        DatabaseUtil(this).add(it)
                    }
                }
                startActivity(Intent(this, MainActivity::class.java))
                dialog.dismiss()
                finish()
            }
        }
        loginViewModel.isShowLoading.observe(this) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
                binding.btnGetCode.isClickable = false
            }
        }
        loginViewModel.errorData.observe(this) {
            it.errMsg?.let { it1 -> CommonUtil.toast(this, it1) }
        }
    }
    override fun initMusic(newIndex: Int) {
        // 这里不需要执行任何操作，留空即可
    }
}
