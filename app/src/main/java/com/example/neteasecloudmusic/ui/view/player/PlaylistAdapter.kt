package com.example.neteasecloudmusic.ui.view.player

import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.neteasecloudmusic.R
import com.example.neteasecloudmusic.ui.model.MusicBean

class PlaylistAdapter(
    private val playlist: List<MusicBean>,
    private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSongTitle: TextView = itemView.findViewById(R.id.textViewSongTitle)
        val textViewArtist: TextView = itemView.findViewById(R.id.textViewArtist)
        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position)
                    setSelectedPosition(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val musicItem = playlist[position]
        holder.textViewSongTitle.text = musicItem.music_name
        holder.textViewArtist.text = musicItem.singer

        if (position == selectedPosition){
            holder.textViewSongTitle.setTextColor(Color.RED)
            holder.textViewArtist.setTextColor(Color.RED)
            holder.textViewSongTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f)
            holder.textViewArtist.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f)
        }else {
            holder.textViewSongTitle.setTextColor(Color.BLACK)
            holder.textViewArtist.setTextColor(Color.parseColor("#AAAAAA"))
            holder.textViewSongTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f)
            holder.textViewArtist.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)
        }
    }

    override fun getItemCount(): Int {
        return playlist.size
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setSelectedPosition(position: Int) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(position)
    }
}