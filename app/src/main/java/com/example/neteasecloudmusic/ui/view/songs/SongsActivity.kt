package com.example.neteasecloudmusic.ui.view.songs

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.api.HttpConfig
import com.example.neteasecloudmusic.databinding.ActivitySongListBinding
import com.example.neteasecloudmusic.ui.base.BaseActivity
import com.example.neteasecloudmusic.ui.model.GlobalUserInfo
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.ui.view.songs.adapter.SongRecycleViewAdapter
import com.example.neteasecloudmusic.ui.viewModel.SongsViewModel
import com.example.neteasecloudmusic.utils.CommonUtil


class SongsActivity : BaseActivity() {
    private lateinit var binding: ActivitySongListBinding
    private lateinit var songsViewModel: SongsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SongRecycleViewAdapter
    private var musicList: ArrayList<MusicBean> = ArrayList()
    private lateinit var menuBean: MenuBean

    override fun getLayout(): View {
        binding = ActivitySongListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initView() {
        menuBean = CommonUtil.getSerializable(this, "menuBean", MenuBean::class.java)
        val layoutManager = LinearLayoutManager(this)
        recyclerView = binding.songRecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                layoutManager.orientation
            )
        )
        setSupportActionBar(binding.storageBoxToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = SongRecycleViewAdapter(this, musicList)//  设置适配器
        adapter.viewModelStoreOwner = this
        recyclerView.adapter = adapter
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.directory_toolbar,menu)
        if (menuBean.uid != GlobalUserInfo.pk_id)
        {
            menu?.findItem(R.id.toolbar_edit)?.isVisible = false
            menu?.findItem(R.id.toolbar_add)?.isVisible = false
            menu?.findItem(R.id.toolbar_deleteIt)?.isVisible = false
        }
        val searchItem : MenuItem? = menu?.findItem(R.id.toolbar_search)
        if (searchItem != null) {
            val searchView: android.widget.SearchView = searchItem?.actionView as android.widget.SearchView
            searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
//                    songsViewModel.getMusicList(menuBean.musicIdList.toString(), menuBean.musicNum)
                    songsViewModel.searchMusic(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    songsViewModel.searchMusic(newText)
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.toolbar_edit ->{
                val intent = Intent(this, EditMenuActivity::class.java).apply {
                    putExtra("menuBean", menuBean)
                }
                myActivityLauncher.launch(intent)
            }
            R.id.toolbar_add ->{
                val intent = Intent(this, AddMenuActivity::class.java).apply {
                    putExtra("menuBean", menuBean)
                }
                myActivityLauncher.launch(intent)
            }
            R.id.toolbar_deleteIt ->{
                songsViewModel.delete(menuBean.pk_id)
                GlobalUserInfo.menuList.apply {
                    this.remove(this.indexOf(menuBean.pk_id))
                }
                val intent = Intent()
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initData() {
        songsViewModel = ViewModelProvider(this)[SongsViewModel::class.java]
        songsViewModel.musicListAll.observe(this) {
            musicList.addAll(it)
            adapter = SongRecycleViewAdapter(this, musicList)//  设置适配器
            recyclerView.adapter = adapter
        }
        songsViewModel.musicListShow.observe(this) {
            musicList.clear()
            musicList.addAll(it)
            adapter = SongRecycleViewAdapter(this, musicList)//  设置适配器
            recyclerView.adapter = adapter
        }
        songsViewModel.menu.observe(this) {
            binding.tvUserName.text = it.username
            binding.tvSonglistName.text = it.menu_name
            binding.tvintroduction.text = it.menu_intro + ">"
            //加载图片
            Glide.with(this)
                .load("${HttpConfig.sourceUrl}${it.cover_image_path}")
                .into(binding.menuImg)
            menuBean.musicIdList?.let { it1 -> songsViewModel.getMusicList(it1,menuBean.musicNum) }
        }
        songsViewModel.isShowLoading.observe(this) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
            }
        }
        songsViewModel.errorData.observe(this) {
            it.errMsg?.let { it1 -> CommonUtil.toast(this, it1) }
        }
        songsViewModel.menu.value = menuBean
        if (menuBean.musicIdList != null || menuBean.musicIdList != "") {
            songsViewModel.getMusicList(menuBean.musicIdList.toString(), menuBean.musicNum)
        }
    }

    private val myActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if(activityResult.resultCode == Activity.RESULT_OK){
            val result = activityResult.data?.getSerializableExtra("menuBean") as MenuBean
            songsViewModel.menu.value = result
        }
    }
    override fun initMusic(newIndex: Int) {
        // 这里不需要执行任何操作，留空即可
    }
}