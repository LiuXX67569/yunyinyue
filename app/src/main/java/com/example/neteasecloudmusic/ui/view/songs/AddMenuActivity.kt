package com.example.neteasecloudmusic.ui.view.songs

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neteasecloudmusic.databinding.ActivityAddMenuBinding
import com.example.neteasecloudmusic.ui.base.BaseActivity
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.MusicShowBean
import com.example.neteasecloudmusic.ui.view.songs.adapter.AddSongRecycleViewAdapter
import com.example.neteasecloudmusic.ui.viewModel.MusicShowViewModel
import com.example.neteasecloudmusic.utils.CommonUtil


class AddMenuActivity : BaseActivity() {
    private lateinit var binding: ActivityAddMenuBinding
    private lateinit var musicShowViewModel: MusicShowViewModel
    private lateinit var recyclerView: RecyclerView
    private var musicList: ArrayList<MusicShowBean> = ArrayList()
    private lateinit var menuBean: MenuBean
    private var searchText: String = ""
    private lateinit var adapter: AddSongRecycleViewAdapter
    override fun getLayout(): View {
        binding = ActivityAddMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        menuBean = CommonUtil.getSerializable(this, "menuBean", MenuBean::class.java)
        setSupportActionBar(binding.addMenuToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView = binding.addSongRecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                layoutManager.orientation
            )
        )
        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 文本变化前的回调
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 文本变化时的回调
                // 在这里处理文本变化的逻辑
            }

            override fun afterTextChanged(s: Editable?) {
                // 文本变化后的回调
                val newText = s?.toString() ?: ""
                menuBean.musicIdList?.let { musicShowViewModel.getMusicShowByName(newText)}
            }
        })

        adapter = AddSongRecycleViewAdapter(this, musicList)//  设置适配器
        adapter.viewModelStoreOwner = this
        recyclerView.adapter = adapter

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                val resultIntent = Intent()
                resultIntent.putExtra("menuBean", menuBean)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }



    override fun initData() {
        musicShowViewModel = ViewModelProvider(this)[MusicShowViewModel::class.java]
        musicShowViewModel.musicShowList.observe(this) {
            menuBean.musicIdList?.let { it1 -> musicShowViewModel.initNumbers(it1) }
            musicList.clear()
            musicList.addAll(it)
            adapter = AddSongRecycleViewAdapter(this, musicList)//  设置适配器
            recyclerView.adapter = adapter
        }
        musicShowViewModel.numbers.observe(this){
            menuBean.musicIdList = musicShowViewModel.getIdListString()
            menuBean.musicNum = musicShowViewModel.getListSize()
            musicShowViewModel.updateMenu(menuBean)
            musicShowViewModel.initializeFlagInMenu()
        }
        musicShowViewModel.isShowLoading.observe(this) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
            }
        }
        musicShowViewModel.errorData.observe(this) {
            it.errMsg?.let { it1 -> CommonUtil.toast(this, it1) }
        }
        menuBean.musicIdList?.let { musicShowViewModel.getMusicShowByName(searchText) }
    }
    override fun initMusic(newIndex: Int) {
        // 这里不需要执行任何操作，留空即可
    }


}
