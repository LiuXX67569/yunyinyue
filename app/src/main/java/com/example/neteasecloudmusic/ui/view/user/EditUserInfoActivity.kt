package com.example.neteasecloudmusic.ui.view.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.api.HttpConfig
import com.example.neteasecloudmusic.databinding.EditUserinfoLayoutBinding
import com.example.neteasecloudmusic.ui.base.BaseActivity
import com.example.neteasecloudmusic.ui.model.GlobalUserInfo
import com.example.neteasecloudmusic.ui.viewModel.UserViewModel
import com.example.neteasecloudmusic.utils.CommonUtil
import com.example.neteasecloudmusic.utils.FileUtil
import com.example.neteasecloudmusic.utils.GlideUtil

class EditUserInfoActivity : BaseActivity() {
    private lateinit var binding: EditUserinfoLayoutBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var alertDialog: AlertDialog
    private var newUserInfo = GlobalUserInfo.getUserInfo()

    override fun getLayout(): View {
        binding = EditUserinfoLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        val option: Array<CharSequence> by lazy { arrayOf("照相机", "相册") }

        setSupportActionBar(binding.editUserInfoToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Glide.with(this)
            .load("${HttpConfig.sourceUrl}${GlobalUserInfo.portrait}")
            .apply(GlideUtil.requestOptions)
            .thumbnail(0.25f)
            .into(binding.imgViPortrait)

        GlobalUserInfo.apply {
            username.let { binding.username.text = it }
            introduction.let { binding.introduction.text = it }
        }

        binding.editUserInfoRlName.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this)
                .inflate(R.layout.alertdialog_edit_layout, null)
            alertDialog = builder
                .setTitle("请输入昵称")
                .setView(view)
                .setCancelable(false)
                .create()
            view.findViewById<Button>(R.id.btn_OK).setOnClickListener {
                val input = view.findViewById<EditText>(R.id.edit).text.toString()
                // 同步
                newUserInfo.username = input
                userViewModel.updateUserInfo(newUserInfo)
            }
            view.findViewById<Button>(R.id.btn_Cancel).setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
        binding.editUserInfoRlIntroduction.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = LayoutInflater.from(this)
                .inflate(R.layout.alertdialog_edit_layout, null)
            alertDialog = builder
                .setTitle("请输入简介")
                .setView(view)
                .setCancelable(false)
                .create()
            view.findViewById<Button>(R.id.btn_OK).setOnClickListener {
                val input = view.findViewById<EditText>(R.id.edit).text.toString()
                // 同步
                newUserInfo.introduction = input
                userViewModel.updateUserInfo(newUserInfo)
            }
            view.findViewById<Button>(R.id.btn_Cancel).setOnClickListener {
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
        binding.imgViPortrait.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("选择图片来源")
                .setItems(option) { _, item ->
                when {
                    option[item] == "照相机" -> {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.CAMERA),
                                FileUtil.MY_PERMISSIONS_REQUEST_CAMERA)
                        } else {
                            runCamera()
                        }
                    }
                    option[item] == "相册" -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                                != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this,
                                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                                    FileUtil.MY_PERMISSIONS_REQUEST_ALBUM
                                )
                            } else {
                                openAlbum()
                            }
                        } else {
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this,
                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                    FileUtil.MY_PERMISSIONS_REQUEST_ALBUM
                                )
                            } else {
                                openAlbum()
                            }
                        }
                    }
                }
            }
                .create()
                .show()
        }
    }

    private fun runCamera() {
        val intent = FileUtil.runCamera(this)
        startActivityForResult(intent, FileUtil.TAKE_PHOTO)
    }

    private fun openAlbum() {
        val intent = FileUtil.openAlbum()
        startActivityForResult(intent, FileUtil.CHOOSE_PHOTO)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FileUtil.MY_PERMISSIONS_REQUEST_CAMERA -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    runCamera()
                } else {
                    CommonUtil.toast(this, "相机权限未授权")
                }
            }
            FileUtil.MY_PERMISSIONS_REQUEST_ALBUM -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openAlbum()
                } else {
                    CommonUtil.toast(this, "相册权限未授权")
                }
            }
            else -> {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FileUtil.TAKE_PHOTO -> if (resultCode == RESULT_OK) {
                userViewModel.upload(
                    FileUtil.cameraPrepareFilePart(
                        this,
                        "image",
                        GlobalUserInfo.email
                    )
                )
            }
            FileUtil.CHOOSE_PHOTO -> if (resultCode == RESULT_OK && data != null) {
                data.data?.let { uri ->
                    userViewModel.upload(
                        FileUtil.albumPrepareFilePart(
                            this,
                            "image",
                            GlobalUserInfo.email,
                            uri
                        )
                    )
                }
            }
            else -> {}
        }
    }

    override fun initData() {
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.userInfo.observe(this) {
            binding.username.text = it.username
            binding.introduction.text = it.introduction
            GlobalUserInfo.setGlobalUserInfo(it)
        }
        userViewModel.imagePath.observe(this) {
            if (it != GlobalUserInfo.portrait) {
                newUserInfo.portrait = it
                userViewModel.updateUserInfo(newUserInfo)
            }
        }
        userViewModel.updateStatusS.observe(this) {
            if (it == 1) {
                userViewModel.updateDatabase(this, newUserInfo)
            } else {
                AlertDialog.Builder(this)
                    .setTitle("错误")
                    .setMessage("服务器异常，更新失败")
                    .setCancelable(true)
                    .create()
                    .show()
            }
        }
        userViewModel.updateStatusC.observe(this) {
            if (it == 1) {
                userViewModel.setUserInfo(newUserInfo)
                if (::alertDialog.isInitialized) {
                    alertDialog.dismiss()
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle("错误")
                    .setMessage("本地数据库异常，更新失败")
                    .setCancelable(true)
                    .create()
                    .show()
            }
        }
        userViewModel.isShowLoading.observe(this) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
            }
        }
        userViewModel.errorData.observe(this) {
            it.errMsg?.let { it1 ->
                CommonUtil.toast(this, it1) }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return true
    }
    override fun initMusic(newIndex: Int) {
        // 这里不需要执行任何操作，留空即可
    }

}