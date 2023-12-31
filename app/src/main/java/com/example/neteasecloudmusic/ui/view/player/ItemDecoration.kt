package com.example.neteasecloudmusic.ui.view.player

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(context: Context, private val spacing: Int) : RecyclerView.ItemDecoration() {

    private val halfSpacing: Int = spacing / 2

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = halfSpacing
        outRect.bottom = halfSpacing
        outRect.left = halfSpacing
        outRect.right = halfSpacing
    }
}