package com.example.neteasecloudmusic.ui.view.songs

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.neteasecloudmusic.databinding.EditMenuLayoutBinding
import com.example.neteasecloudmusic.ui.base.BaseActivity
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.viewModel.MenuEditViewModel
import com.example.neteasecloudmusic.utils.CommonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class EditMenuActivity : BaseActivity() {
    private lateinit var binding: EditMenuLayoutBinding
    private lateinit var menuEditViewModel: MenuEditViewModel
    private lateinit var menuBean: MenuBean
    override fun getLayout(): View {
        binding = EditMenuLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        menuBean = CommonUtil.getSerializable(this, "menuBean", MenuBean::class.java)
        //加载图片
        Glide.with(this)
            .load(menuBean.cover_image_path)
            .into(binding.editMenuImg)

        binding.editMenuName.setText(menuBean.menu_name)
        binding.editMenuIntro.setText(menuBean.menu_intro)
        binding.editMenuImg.setOnClickListener {
            //打开文件选择器
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            //指定只显示图片
            intent.type = "image/*"
            someActivityResultLauncher.launch(intent)
        }
        setSupportActionBar(binding.editMenuToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.btnMenuUpdate.setOnClickListener{
            menuBean.menu_intro = binding.editMenuIntro.text.toString()
            menuBean.menu_name = binding.editMenuName.text.toString()
            Toast.makeText(this, "编辑成功", Toast.LENGTH_SHORT).show()
            menuEditViewModel.updateMenu(menuBean)
        }
    }
    private val someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            // 处理结果
            val data = result.data
            if (data != null) {
                data.data?.let {  uri->
                    val bitmap = getBitmapFromUri(uri)
                    menuBean.cover_image_path = uri.toString()
//                    binding.editMenuIntro.setText(uri.toString())
                    binding.editMenuImg.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                val resultIntent = Intent()
                resultIntent.putExtra("menuBean", menuBean)
//                Toast.makeText(this, "${menuBean.menu_intro}", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    override fun initData() {
        menuEditViewModel = ViewModelProvider(this)[MenuEditViewModel::class.java]
        menuEditViewModel.errorData.observe(this) {
            it.errMsg?.let { it1 -> CommonUtil.toast(this, it1) }
        }
        menuEditViewModel.isShowLoading.observe(this) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
            }
        }
    }

    suspend fun loadImage() {
        val bitmap = withContext(Dispatchers.IO) {
            getBitmapFromUri(Uri.parse(menuBean.cover_image_path))
        }
        withContext(Dispatchers.Main) {
            binding.editMenuImg.setImageBitmap(bitmap)
        }
    }
    override fun initMusic(newIndex: Int) {
        // 这里不需要执行任何操作，留空即可
    }
}