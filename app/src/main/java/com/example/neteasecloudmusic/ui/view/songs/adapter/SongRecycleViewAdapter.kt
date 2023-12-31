package com.example.neteasecloudmusic.ui.view.songs.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.ui.base.BaseRecyclerViewAdapter
import com.example.neteasecloudmusic.ui.base.BaseViewHolder
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.ui.view.songs.SongsActivity
import com.example.neteasecloudmusic.ui.viewModel.SongsViewModel

class SongRecycleViewAdapter() : BaseRecyclerViewAdapter<SongRecycleViewAdapter.MyViewHolder> () {

    var viewModelStoreOwner: SongsActivity? = null
    private lateinit var viewModel: SongsViewModel

    constructor(context: Context, dataList: ArrayList<*>) : this() {
        this.context = context
        this.dataList = dataList
    }

    inner class MyViewHolder(itemView: View) : BaseViewHolder(itemView){
        fun setData(context: Context, view: View, musicBean: MusicBean) {
            view.findViewById<TextView>(R.id.tvSong_name).text = musicBean.music_name
            view.findViewById<TextView>(R.id.tvSinger_name).text = musicBean.singer
            view.findViewById<TextView>(R.id.tvAlbum_name).text = musicBean.album
            view.findViewById<ImageButton>(R.id.tvSongsImgBtn).apply {
                this.isVisible = viewModel.flag
                this.setOnClickListener {
                    viewModelStoreOwner?.let { it1 ->
                        val sss = arrayOf("删除歌曲出歌单", "删除歌曲下载文件")
                        var result:Int = -2
                        val dialog : AlertDialog = AlertDialog.Builder(it1).setCancelable(false)
                            .setTitle("提示")
                            .setSingleChoiceItems(sss, -1) {_, which ->
                                result = which
                            }
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定") { _, _ ->
                                if (result == 0)
                                {
                                    //删除歌曲
                                    viewModel.unLike(musicBean.pk_id)
                                }
                                else if (result == 1)
                                {
                                    //删除下载

                                }
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
        val tvIndex: TextView = itemView.findViewById(R.id.tvIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.song_elem_layout, parent, false)
        viewModelStoreOwner = context as SongsActivity?
        viewModel = ViewModelProvider(viewModelStoreOwner!!)[SongsViewModel::class.java]
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        context?.let {
            (holder as MyViewHolder).setData(
                it,
                holder.itemView,
                dataList?.get(position) as MusicBean
            )
        }
        val index = position + 1
        (holder as MyViewHolder).tvIndex.text = "$index"
    }
}