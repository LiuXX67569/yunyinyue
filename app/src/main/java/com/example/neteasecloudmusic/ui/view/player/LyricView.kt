package com.example.neteasecloudmusic.ui.view.player

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Message
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.OvershootInterpolator
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView

class LyricView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnTouchListener {
    private var mEmptyView: View? = null
    private var mTextView: TextView? = null
    private var mScrollView: ScrollView? = null
    private var autoScroll = false
    private var userTouch = false
    private var paddingValue = 0
    private var mPosition = 0
    private var resetType = 1
    private val MSG_USER_TOUCH = 0x349
    private var lyricInfo: LyricInfo? = null
    
    init {
        initWithContext(context)
    }

    //设置歌词信息（setLyricInfo）
    fun setLyricInfo(lyricInfo: LyricInfo?) {
        this.lyricInfo = lyricInfo
        updateLyricsText()//更新歌词文本内容
    }

    private fun updateLyricsText() {
        val lyricsText = getLyricsText(lyricInfo)
        setText(lyricsText)//调用 setText 方法将歌词文本设置到 mTextView 中
    }




    private fun getLyricsText(lyricInfo: LyricInfo?): CharSequence? {
        return lyricInfo?.let {
            val builder = StringBuilder()
            for (line in it.songLines) {
                builder.append(line.content).append("\n")
            }
            val lyricsText = builder.toString()
            lyricsText
        }
    }

    //通过 setEmptyView 方法设置空视图，用于在歌词为空时显示。
    //根据歌词内容是否为空来控制空视图的可见性。
    fun setEmptyView(emptyView: View) {
        mEmptyView = emptyView
        if (mEmptyView != null && mTextView != null) {
            if ((mTextView!!.text.toString() == null || "" == mTextView!!.text.toString().trim())) {
                mEmptyView!!.visibility = View.VISIBLE
            } else {
                mEmptyView!!.visibility = View.GONE
            }
        }
    }

    fun getEmptyView(): View? {
        return mEmptyView
    }

    private fun initWithContext(context: Context) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                paddingValue =
                    (height - paddingBottom - paddingTop) / 2
                if (mTextView != null && mScrollView != null) {
                    mTextView!!.setPadding(0, paddingValue, 0, paddingValue)
                    mScrollView!!.fullScroll(ScrollView.FOCUS_UP)
                }
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    //通过 setCurrentPosition 方法设置当前歌词的位置。
    //调用 doScroll 方法执行歌词滚动。
    fun setCurrentPosition(position: Int) {
        if (mPosition != position) {
            mPosition = position
            highlightCurrentLine(position) // 高亮显示当前行
            doScroll(position)
//            if (!userTouch) {
//                Log.d("LyricView", "Setting current position: $mPosition")
//                doScroll(position)
//            }
        }
    }

    private fun highlightCurrentLine(position: Int) {
        if (mTextView != null && lyricInfo != null) {
            val layout = mTextView!!.layout

            if (layout != null) {
                val lines = mTextView!!.text.toString().split("\n")

                val spannable = SpannableStringBuilder(mTextView!!.text)

                for ((index, line) in lines.withIndex()) {
                    val lineStart = layout.getLineStart(index)
                    val lineEnd = layout.getLineEnd(index)

                    val span = if (index == position) {
                        ForegroundColorSpan(Color.WHITE)
                    } else {
                        val darkGreyColor = Color.parseColor("#d0d0d0")
                        ForegroundColorSpan(darkGreyColor)
                    }

                    val textSizeSpan = if (index == position) {
                        AbsoluteSizeSpan(23, true)
                    } else {
                        AbsoluteSizeSpan(21, true)
                    }

                    val textStyleSpan = if (index == position) {
                        StyleSpan(Typeface.BOLD)
                    } else {
                        StyleSpan(Typeface.NORMAL)
                    }
                    spannable.setSpan(span, lineStart, lineEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(textSizeSpan, lineStart, lineEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannable.setSpan(textStyleSpan, lineStart, lineEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                mTextView!!.text = spannable
            }
        }
    }


    //布局初始化
    //初始化 mScrollView 和 mTextView。
    //设置 mTextView 的样式和属性，以及触摸监听器。
    override fun onFinishInflate() {
        super.onFinishInflate()
        mScrollView = ScrollView(context)
        val scrollViewParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mScrollView!!.overScrollMode = ScrollView.OVER_SCROLL_NEVER
        mScrollView!!.isVerticalScrollBarEnabled = false
        mScrollView!!.layoutParams = scrollViewParams
        mScrollView!!.isVerticalFadingEdgeEnabled = true
        mScrollView!!.setOnTouchListener(this)
        mScrollView!!.setFadingEdgeLength(220)

        mTextView = TextView(context)
        mTextView!!.gravity = Gravity.CENTER
        mTextView!!.textSize = 21.0f
        mTextView!!.setLineSpacing(6f, 1.5f)
        mTextView!!.setPadding(0, paddingValue, 0, paddingValue)
        val darkGreyColor = Color.parseColor("#d0d0d0")
        mTextView!!.setTextColor(darkGreyColor)  // 添加这一行，设置字体颜色为白色

        mScrollView!!.addView(
            mTextView,
            android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )


        addView(mScrollView)


    }

    //设置歌词文本内容，并根据内容是否为空来控制空视图的可见性。
    fun setText(charSequence: CharSequence?) {
        if (mTextView != null) {
            mTextView!!.text = charSequence
            if (charSequence == null || "" == charSequence.toString().trim()) {
                if (mEmptyView != null) {
                    mEmptyView!!.visibility = View.VISIBLE
                }
            } else {
                if (mEmptyView != null) {
                    mEmptyView!!.visibility = View.GONE
                }
            }
        }
        Log.d("setText","显示的文本：\n${mTextView?.text}")
    }

    override fun setOnLongClickListener(longClickListener: View.OnLongClickListener?) {
        if (mTextView != null) {
            mTextView!!.setOnLongClickListener(longClickListener)
        }
    }

    fun getLineHeight(): Int {
        Log.e(javaClass.name.toString(), "**********************" + mTextView!!.lineHeight)
        return mTextView!!.lineHeight
    }


    fun getLineCount(): Int {
        return mTextView!!.lineCount
    }

    fun getTextView(): TextView {
        return mTextView!!
    }

    override fun getPaddingTop(): Int {
        return mTextView!!.paddingTop
    }

    private fun doScroll(position: Int) {
        if (mScrollView != null) {
            val animator = setupScroll(position)
            animator.start()
//            if (!userTouch) {
//                Log.d("LyricView", "Performing scroll for position: $position")
//                val animator = setupScroll(position)
//                animator.start()
//            }
        }
    }

    private fun setupScroll(position: Int): Animator {
        if (mTextView == null) {
            return ObjectAnimator.ofInt(0, 0) // 返回一个空动画，表示无需滚动
        }

        val lineHeight = mTextView!!.lineHeight
        val paddingTop = mTextView!!.paddingTop

        val startY = mScrollView!!.scrollY
        val endY = lineHeight * position + paddingTop - height / 2 + lineHeight / 2
        Log.d("每行位置", "Setting up scroll animation. StartY: $startY, EndY: $endY")
        val animator = ValueAnimator.ofInt(startY, endY)
        animator.duration = 600
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            mScrollView!!.smoothScrollTo(0, value)
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                handler.sendEmptyMessageDelayed(MSG_USER_TOUCH, 1000)
            }
        })
        return animator
    }




    private var downY = 0f

    //触摸事件处理
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> down(event)
            MotionEvent.ACTION_MOVE -> move(event)
            MotionEvent.ACTION_UP -> up(event)
        }
        return false
    }

    private fun down(event: MotionEvent) {
        Log.e("Log.e", "*********************Down")
        downY = event.y
        handler.removeMessages(MSG_USER_TOUCH)
        userTouch = true
    }

    private fun move(event: MotionEvent) {
        val moveY = Math.abs(event.y - downY)
        if (mScrollView!!.scrollY <= 0 && (event.y - downY) > 0) {
            resetType = 1
            autoScroll = true
            mTextView!!.setPadding(
                mTextView!!.paddingLeft,
                (moveY / 1.8f + mTextView!!.paddingTop).toInt(),
                mTextView!!.paddingRight,
                mTextView!!.paddingBottom
            )
        }
        if (mScrollView!!.scrollY >= (mTextView!!.height - mScrollView!!.height - mScrollView!!.paddingTop - mScrollView!!.paddingBottom) && (event.y - downY) < 0
        ) {
            resetType = -1
            autoScroll = true
            mTextView!!.setPadding(
                mTextView!!.paddingLeft,
                mTextView!!.paddingTop,
                mTextView!!.paddingRight,
                (moveY / 1.2f + mTextView!!.paddingBottom).toInt()
            )
            mScrollView!!.fullScroll(ScrollView.FOCUS_DOWN)
        }
        downY = event.y
    }

    private fun up(event: MotionEvent) {
        resetViewHeight()
    }

    //重置视图高度,在触摸结束后，根据滚动方向和 mTextView 的 padding 值来判断是否需要重置视图高度。
    //调用 reset 方法执行高度重置，使用动画效果平滑过渡。
    fun resetViewHeight() {
        if (autoScroll && (mTextView!!.paddingBottom > paddingValue || mTextView!!.paddingTop > paddingValue)) {
            reset()
        } else {
            handler.sendEmptyMessageDelayed(MSG_USER_TOUCH, 1200)
        }
    }

    //重置，使用 ValueAnimator 实现平滑的高度重置动画。
    //根据滚动方向和动画值更新 mTextView 的 padding。
    //动画结束后通过 handler 发送延时消息，以恢复自动滚动。
    fun reset() {
        val animator: ValueAnimator
        if (resetType == 1) {
            animator = ValueAnimator.ofFloat(mTextView!!.paddingTop.toFloat(), paddingValue.toFloat())
            animator.interpolator = OvershootInterpolator(0.7f)
            animator.duration = 400
            animator.addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                mTextView!!.setPadding(
                    mTextView!!.paddingLeft,
                    value.toInt(),
                    mTextView!!.paddingRight,
                    mTextView!!.paddingBottom
                )
                mScrollView!!.fullScroll(ScrollView.FOCUS_UP)
            }
        } else {
            animator = ValueAnimator.ofFloat(mTextView!!.paddingBottom.toFloat(), paddingValue.toFloat())
            animator.interpolator = OvershootInterpolator(0.7f)
            animator.duration = 400
            animator.addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                mTextView!!.setPadding(
                    mTextView!!.paddingLeft,
                    mTextView!!.paddingTop,
                    mTextView!!.paddingRight,
                    value.toInt()
                )
                mScrollView!!.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
        animator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                handler.sendEmptyMessageDelayed(MSG_USER_TOUCH, 1200)
            }
        })
        animator.start()
    }

    //Handler 处理自动滚动
    private val handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_USER_TOUCH -> userTouch = false
            }
        }
    }
}
