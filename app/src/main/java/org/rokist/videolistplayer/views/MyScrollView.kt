package org.rokist.videolistplayer.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Choreographer
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import kotlin.math.max
import kotlin.math.min


typealias Hoge = (l: Int, t: Int, oldl: Int, oldt: Int) -> Unit
typealias OnFlingListener = (velocityY: Int) -> Unit


interface ITouchClass {
    fun onTouch(event: MotionEvent, scrollView: MyScrollView): Boolean
    fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int, scrollView: MyScrollView)
}

/**
 * fowaeijfaowijeaowej
 */
open class MyScrollFrame(context: Context) : FrameLayout(context) {
    companion object {
        private const val MAX_THUMB_HEIGHT_PERCENT = 0.15
        private const val MIN_THUMB_HEIGHT_PERCENT = 0.03
    }

    private var _bar: LinearLayout
    private var _startParams = LayoutParams(0,0)
    private var _endParams = LayoutParams(0, 0)

    private var _view: View? = null
    private var _thumb: LinearLayout
    private var _scrollView = MyScrollView(context)
    fun getScrollView(): MyScrollView {
        return _scrollView
    }

    private val scrollBarWidth: Int = (5 * ViewSettings.density).toInt()
    private var _scrollOffset = 0

    fun setSubView(view: View) {
        _view = view

        this.addView(view, 1)
    }


    private var _marginTop = 0
    private var _marginBottom = 0

    init {
        _bar = LinearLayout(context).also {
            it.setBackgroundColor(Color.argb(0, 55, 33, 115))
            it.layoutParams = LayoutParams(scrollBarWidth, MATCH_PARENT, Gravity.END)
        }

        _thumb = LinearLayout(context).also {
            it.setBackgroundColor(Color.argb(183, 55, 0, 215))
            it.layoutParams = LayoutParams(scrollBarWidth, 30, Gravity.END)
        }

        _scrollView.setOnScrollChangedForBar { a, yOffset, c, d ->
            _scrollOffset = yOffset
            _refreshScrollBar()
        }

        this.addView(_scrollView)
        this.addView(_bar)
        this.addView(_thumb)
    }

    fun setScrollBarTopOffset(topOffset: Int, bottomOffset: Int) {
        _marginTop = topOffset
        _marginBottom = bottomOffset
        _refreshScrollBar()
    }


    private var animateStartTime = 0L
    private val AnimateTime = 200//220

    fun refreshScrollBar() {
        animateStartTime = System.currentTimeMillis()

        this._startParams.height = _thumb.layoutParams.height
        this._startParams.topMargin = (_thumb.layoutParams as LayoutParams).topMargin

        animateScrollBar()
    }

    private fun animateScrollBar() {
        val diff = System.currentTimeMillis() - animateStartTime
        if (diff > this.AnimateTime) {
            _refreshScrollBar()
            return
        }

        this.applyLatestThumbPos(_endParams)

        val rate = diff / this.AnimateTime.toFloat()

        (_thumb.layoutParams as LayoutParams).let {
            it.topMargin = (_startParams.topMargin +  rate * (_endParams.topMargin - _startParams.topMargin)).toInt()
            it.height = (_startParams.height +  rate * (_endParams.height - _startParams.height )).toInt()
        }
        _thumb.layoutParams = _thumb.layoutParams

        Choreographer.getInstance().postFrameCallback {
            animateScrollBar()
        }
    }

    private fun applyLatestThumbPos(params: LayoutParams) {
        val yOffset = _scrollOffset

        val contentHeight = _scrollView.getChildAt(0).height
        val viewportHeight = _scrollView.height
        val scrollBarHeight = _scrollView.height - _marginTop - _marginBottom

        val thumbHeightPercent = min(
            MAX_THUMB_HEIGHT_PERCENT,
            max(MIN_THUMB_HEIGHT_PERCENT, viewportHeight / contentHeight.toDouble())
        )

        val thumbHeight = (thumbHeightPercent * scrollBarHeight).toInt()

        val scrollBarRange = scrollBarHeight - thumbHeight
        val scrollBottomOffset = contentHeight - viewportHeight
        val scrolledRate = yOffset / scrollBottomOffset.toDouble()

        params.height = thumbHeight
        params.topMargin = _marginTop +  (scrollBarRange * scrolledRate).toInt()
    }

    private fun _refreshScrollBar() {
        applyLatestThumbPos(_thumb.layoutParams as LayoutParams)
        _thumb.layoutParams = _thumb.layoutParams
    }


    fun applyTouchFuncs(touchFuncs: ITouchClass) {
        _scrollView.onTouchFuncs = touchFuncs
    }

}

open class MyScrollView(context: Context) : ScrollView(context) {

    var onTouchFuncs = object : ITouchClass {
        override fun onTouch(event: MotionEvent, scrollView: MyScrollView): Boolean {
            return true;
        }

        override fun onScrollChanged(
            l: Int,
            t: Int,
            oldl: Int,
            oldt: Int,
            scrollView: MyScrollView
        ) {

        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
    }


    val density = context.resources.displayMetrics.density.also {
        if (it <= 1)
            1f
        else if (it > 100)
            100f
        else
            it
    }
    val max = (31000.0) * (density / 2.55)

    var scrollEnabled: Boolean = true
        set(value) {
            field = value
            if (value) {
                // what should we do?
            }
        }

    private var _onScrollChangedForBar: Hoge? = null
    fun setOnScrollChangedForBar(a: Hoge) {
        this._onScrollChangedForBar = a
    }

    private var _onScrollChanged: Hoge? = null
    private var onFlingHandler: OnFlingListener? = null

    fun setOnScrollChanged(a: Hoge) {
        this._onScrollChanged = a
    }

    fun setOnFling(handler: OnFlingListener) {
        this.onFlingHandler = handler
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        this.onTouchFuncs.onScrollChanged(l, t, oldl, oldt, this)
        this._onScrollChanged?.invoke(l, t, oldl, oldt)
        this._onScrollChangedForBar?.invoke(l, t, oldl, oldt)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        /*if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            return true
        }*/

        return this.scrollEnabled
                && onTouchFuncs.onTouch(event, this)
                && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.actionMasked == MotionEvent.ACTION_DOWN) {
            //return true
        }

        return this.scrollEnabled && super.onInterceptTouchEvent(ev)
    }

    override fun fling(velocityY: Int) {
        //Log.d("aaa", "d = ${l.pointer}")
        //context.toActivity().doWithText("abc\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC67{}[]_?><日本語@!test")
        /*
        val topVelocityY = (Math.min(
            Math.abs(velocityY),
            100000
        ) * Math.signum(velocityY.toFloat())).toInt()
        super.fling((topVelocityY * 1.5).toInt())
        */

        var newVel = (velocityY * 1.6) //2.0


        if (newVel > max) {
            newVel = max
        } else if (newVel < -max) {
            newVel = -max
        }
        //Log.d("aaa", newVel.toString())

        //val maxFlingVelocity =ViewConfiguration.get(this.context).scaledMaximumFlingVelocity.toFloat()
        //Log.d("aaa", context.resources.displayMetrics.density.toString())
        this.onFlingHandler?.invoke(newVel.toInt())
        super.fling(newVel.toInt())
/*
        super.fling(velocityY)
 */
    }
}