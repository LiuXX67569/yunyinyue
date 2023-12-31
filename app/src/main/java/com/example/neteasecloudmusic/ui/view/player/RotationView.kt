package com.example.neteasecloudmusic.ui.view.player

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatImageView
import com.example.neteasecloudmusic.R

class RotationView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    private var rotationDrawable: Drawable? = null
    private var rotationAnimator: ObjectAnimator? = null
    private var rotationDegrees = 0f
    private var isAnimationPaused = false
    private var coverImagePath: String? = null

    init {
        rotationDrawable = context.getDrawable(R.drawable.testimg)
    }

    override fun setImageResource(resId: Int) {
        rotationDrawable = context.getDrawable(resId)
        invalidate()
    }

    fun setRotationDrawable(drawable: Drawable?) {
        rotationDrawable = drawable
        invalidate()
    }

    fun setCoverImagePath(path: String) {
        coverImagePath = path
        loadCoverImage()
    }

    private fun loadCoverImage() {
        if (coverImagePath != null && coverImagePath!!.isNotEmpty()) {
            // 使用 Glide 加载图片资源
            // Glide.with(context).load(coverImagePath).into(this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2
        val centerY = height / 2
        val radius = Math.min(centerX, centerY).toFloat()
        val path = Path()
        path.addCircle(centerX.toFloat(), centerY.toFloat(), radius, Path.Direction.CW)
        canvas.clipPath(path)

        rotationDrawable?.let {
            it.setBounds(0, 0, width, height)
            it.draw(canvas)
        }
    }
    fun createRotation(): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, "rotation", rotationDegrees, rotationDegrees + 360f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    fun startRotation() {
        rotationAnimator = createRotation()
        rotationAnimator?.start()
        isAnimationPaused = false
    }

    fun pauseRotation() {
        rotationAnimator?.let {
            it.pause()
            isAnimationPaused = true
        }
    }
    fun resumeRotation() {
        rotationAnimator?.let {
            it.resume()
            isAnimationPaused = false
        }
    }


    fun getFlag():Boolean{
        return isAnimationPaused
    }

    fun setFlag(){
        isAnimationPaused = false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pauseRotation()
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putFloat("rotationDegrees", rotationDegrees)
        bundle.putBoolean("isAnimationPaused", isAnimationPaused)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            rotationDegrees = state.getFloat("rotationDegrees")
            isAnimationPaused = state.getBoolean("isAnimationPaused")
            super.onRestoreInstanceState(state.getParcelable("superState"))
        } else {
            super.onRestoreInstanceState(state)
        }
    }
}
