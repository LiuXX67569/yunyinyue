package com.example.neteasecloudmusic.ui.view.main.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.api.HttpConfig
import com.example.neteasecloudmusic.databinding.MineLayoutBinding
import com.example.neteasecloudmusic.ui.base.BaseFragment
import com.example.neteasecloudmusic.ui.model.GlobalUserInfo
import com.example.neteasecloudmusic.ui.model.MenuBean
import com.example.neteasecloudmusic.ui.view.search.adapter.MenuRecycleViewAdapter
import com.example.neteasecloudmusic.ui.view.songs.SongsActivity
import com.example.neteasecloudmusic.ui.view.user.EditUserInfoActivity
import com.example.neteasecloudmusic.ui.viewModel.MineFragmentViewModel
import com.example.neteasecloudmusic.utils.CommonUtil

class MineFragment : BaseFragment() {
    private lateinit var binding: MineLayoutBinding
    private lateinit var menuRecycleViewAdapter: MenuRecycleViewAdapter
    private lateinit var mineFragmentViewModel: MineFragmentViewModel
    private lateinit var view: View
    private lateinit var context: Context

    override fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = MineLayoutBinding.inflate(layoutInflater)
        view = binding.root
        context = view.context

        Glide.with(this)
            .load("${HttpConfig.sourceUrl}${GlobalUserInfo.portrait}")
            .thumbnail(0.25f)
//            .apply(GlideUtil.requestOptions)
            .into(binding.imgViPortrait)

        binding.imgViPortrait.setOnClickListener {
            val intent = Intent(context, EditUserInfoActivity::class.java)
            myActivityLauncher.launch(intent)
        }

        binding.tvUsername.text = GlobalUserInfo.username
        binding.tvFollow.text = "${GlobalUserInfo.num_follow} 关注"
        binding.tvFans.text = "${GlobalUserInfo.num_fans} 粉丝"
        binding.tvLevel.text = "Lv.${GlobalUserInfo.level}"

        binding.imgBtnCreateMenu.setOnClickListener {
            if (GlobalUserInfo.pk_id == -1) {
                // 返回登录
            } else {
                val alertView = LayoutInflater.from(context)
                    .inflate(R.layout.alertdialog_createmenu_layout, null)
                val alertDialog = AlertDialog.Builder(context)
                    .setTitle("创建歌单")
                    .setView(alertView)
                    .setCancelable(false)
                    .create()
                alertView.findViewById<Button>(R.id.btn_OK).setOnClickListener {
                    val menu = MenuBean()
                    menu.menu_name = alertView.findViewById<EditText>(R.id.edit_menuName).text.toString()
                    menu.menu_intro = alertView.findViewById<EditText>(R.id.edit_intro).text.toString()
                    menu.uid = GlobalUserInfo.pk_id
                    menu.username = GlobalUserInfo.username
                    mineFragmentViewModel.addMenu(menu)
                    alertDialog.dismiss()
                }
                alertView.findViewById<Button>(R.id.btn_Cancel).setOnClickListener {
                    alertDialog.dismiss()
                }
                alertDialog.show()
            }
        }

        initMyMenu()

        initViewModel()
        return view
    }

    private fun initMyMenu() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val recyclerView = view.findViewById<RecyclerView>(R.id.myMenu)
        recyclerView.layoutManager = layoutManager
        val arrayList: ArrayList<MenuBean> = ArrayList()
        menuRecycleViewAdapter = MenuRecycleViewAdapter(context, arrayList)
        recyclerView.adapter = menuRecycleViewAdapter
        menuRecycleViewAdapter.setOnClick(object:
            MenuRecycleViewAdapter.MenuRecycleViewAdapterOnClickListener {
            override fun tvOnClickListener(view: View, menuBean: MenuBean) {
                view.setOnClickListener {
                    val intent = Intent(context, SongsActivity::class.java)
                    intent.putExtra("menuBean", menuBean)
                    startActivity(intent)
                }
            }
        })
    }

    private fun initViewModel() {
        mineFragmentViewModel = ViewModelProvider(this)[MineFragmentViewModel::class.java]
        mineFragmentViewModel.menuId.observe(this) {
            if (it == 0) {
                CommonUtil.toast(context, "重命名，歌单已存在")
            } else {
                GlobalUserInfo.menuList.add(it)
                mineFragmentViewModel.getMenuByMenuIdList(CommonUtil.encode(GlobalUserInfo.run {
                    this.parseListIntToStr(this.menuList)
                }))
                mineFragmentViewModel.updateDatabase(context, GlobalUserInfo.getUserInfo())
            }
        }
        mineFragmentViewModel.menuList.observe(this) {
            menuRecycleViewAdapter.setData(ArrayList(it))
        }

        mineFragmentViewModel.getMenuByMenuIdList(CommonUtil.encode(GlobalUserInfo.run {
            this.parseListIntToStr(this.menuList)
        }))
    }

    private val myActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            Activity.RESULT_OK -> {
                binding.tvUsername.text = GlobalUserInfo.username
                Glide.with(this)
                    .clear(binding.imgViPortrait)
                Glide.with(this)
                    .load("${HttpConfig.sourceUrl}${GlobalUserInfo.portrait}")
                    .thumbnail(0.25f)
//            .apply(GlideUtil.requestOptions)
                    .into(binding.imgViPortrait)
            }
            Activity.RESULT_CANCELED -> {
                mineFragmentViewModel.updateDatabase(context, GlobalUserInfo.getUserInfo())
            }
        }
    }

    override fun setData(dataList: List<*>) {

    }
}