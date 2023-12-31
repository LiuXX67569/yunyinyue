package com.example.neteasecloudmusic.ui.view.search.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.api.RetrofitClient
import com.example.neteasecloudmusic.ui.base.BaseFragment
import com.example.neteasecloudmusic.ui.model.MusicBean
import com.example.neteasecloudmusic.ui.view.search.adapter.MusicRecycleViewAdapter
import com.example.neteasecloudmusic.utils.CommonUtil
import com.example.neteasecloudmusic.utils.FileUtil
import com.example.neteasecloudmusic.utils.LogUtil
import com.example.neteasecloudmusic.utils.error.ErrorUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import kotlin.concurrent.thread

class MusicFragment : BaseFragment() {
    private lateinit var musicRecycleViewAdapter: MusicRecycleViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var context: Context
    private var musicBeanList: ArrayList<MusicBean> = ArrayList()

    override fun initView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.music_layout, container, false)
        context = view.context
        recyclerView = view.findViewById(R.id.music_recycleView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        musicRecycleViewAdapter = MusicRecycleViewAdapter(context, musicBeanList)
        recyclerView.adapter = musicRecycleViewAdapter
        musicRecycleViewAdapter.setOnClick(object:
            MusicRecycleViewAdapter.MusicRecycleViewAdapterOnClickListener {
            override fun linearLayoutOnClickListener(view: View, musicBean: MusicBean) {
                view.setOnClickListener {
                    CommonUtil.toast(context, musicBean.toString())
                    RetrofitClient.create().downloadMusic(musicBean.mp3_file_path!!).enqueue(object: retrofit2.Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.let {
                                    val uri = FileUtil.getUriForFile(activity!!.applicationContext, "test.mp3")
                                    thread {
                                        val outputStream = activity!!.contentResolver.openOutputStream(uri)
                                        val buffer = ByteArray(1024)
                                        var len: Int
                                        while (it.byteStream().read(buffer).also {it1-> len = it1 } != -1) {
                                            outputStream?.write(buffer, 0, len)
                                        }
                                        outputStream?.flush()
                                        outputStream?.close()
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            val e = ErrorUtil.getError(t)
                            LogUtil.log(e.errMsg!!)
                        }
                    })
                }
            }

        })
        return  view
    }

    override fun setData(dataList: List<*>) {
        musicBeanList.clear()
        musicBeanList.addAll(dataList.filterIsInstance<MusicBean>() as ArrayList<MusicBean>)
        if (::musicRecycleViewAdapter.isInitialized) {
            musicRecycleViewAdapter = MusicRecycleViewAdapter(context, musicBeanList)
            recyclerView.adapter = musicRecycleViewAdapter
        }
    }



}