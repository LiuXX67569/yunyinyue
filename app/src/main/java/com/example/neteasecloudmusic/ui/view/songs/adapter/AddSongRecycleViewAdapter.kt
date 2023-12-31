package com.example.neteasecloudmusic.ui.view.songs.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.ui.base.BaseRecyclerViewAdapter
import com.example.neteasecloudmusic.ui.base.BaseViewHolder
import com.example.neteasecloudmusic.ui.model.MusicShowBean
import com.example.neteasecloudmusic.ui.view.songs.AddMenuActivity
import com.example.neteasecloudmusic.ui.viewModel.MusicShowViewModel


class AddSongRecycleViewAdapter : BaseRecyclerViewAdapter<AddSongRecycleViewAdapter.MyViewHolder> {

    var viewModelStoreOwner: AddMenuActivity? = null

    constructor(context: Context, dataList: ArrayList<*>) : this() {
        this.context = context
        this.dataList = dataList
    }

    constructor()

    inner class MyViewHolder(itemView: View) : BaseViewHolder(itemView){
        fun setData(context: Context, view: View, musicBean: MusicShowBean) {
            view.findViewById<TextView>(R.id.tvSong_name).text = musicBean.music_name
            view.findViewById<TextView>(R.id.tvSinger_name).text = musicBean.singer
            view.findViewById<TextView>(R.id.tvAlbum_name).text = musicBean.album
            view.findViewById<ImageView>(R.id.tvAdd_img).apply {
                if (musicBean.flagInMenu) {
                    this.setImageResource(R.drawable.img_likes)
                } else {
                    this.setImageResource(R.drawable.icon_heart)
                }
                this.setOnClickListener {
                    viewModelStoreOwner?.let { it1 ->
                        val dialog : AlertDialog = AlertDialog.Builder(it1).setCancelable(false)
                            .setTitle("提示")
                            .setMessage("是否要添加至歌单")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定") { _, _ ->
                                viewModel.like(musicBean.pk_id)
                            }
                            .create()
                        dialog.show()

                        // 设置按钮字体颜色
                        val btnPos : Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        btnPos.setTextColor(context.getColor(R.color.red))
                        val btnNeg : Button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                        btnNeg.setTextColor(context.getColor(R.color.black))
                    }
                }
            }
        }
    }

    private lateinit var viewModel: MusicShowViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.song_add_elem_layout, parent, false)
        viewModelStoreOwner = this.context as AddMenuActivity?
        viewModel = ViewModelProvider(viewModelStoreOwner!!)[MusicShowViewModel::class.java]
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        context?.let {
            (holder as MyViewHolder).setData(
                it,
                holder.itemView,
                dataList?.get(position) as MusicShowBean
            )
        }
    }
}