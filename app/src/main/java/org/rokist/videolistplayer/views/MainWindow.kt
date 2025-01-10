package org.rokist.videolistplayer.views

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.widget.Toast
import android.util.AttributeSet
import android.view.*
import org.rokist.videolistplayer.*


class ViewSettings constructor() {
    private val originLineHeight: Float = 6.7F
    var lineHeight = 30

    var centerBigHeight = 1
    var toolbarHeight = 1

    var screenWidth = 0
    var screenHeight = 0
    var density: Float = 0f

    fun changeLinesForTargetHeight(width: Int, height: Int, density: Float) {
        // this.screenHeight = (height).toInt()
        this.density = density
        ViewSettings.density = density;
        ViewSettings.densityInt = density.toInt()
        ViewSettings.toolBarWidth = (62 * ViewSettings.density).toInt()

        // this.screenWidth = width
        this.lineHeight = ((this.originLineHeight * density).toInt() * 2) // make it even number
        this.centerBigHeight = lineHeight * 4
        this.toolbarHeight = lineHeight * 3
    }

    companion object{
        var density: Float = 0f
        var densityInt: Int = 0
        var toolBarWidth = 0

        fun xDensity(f: Int): Int {
            return (f * density).toInt()
        }

    }
}


class MainWindow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var viewPortWidth: Int = 0
    private var viewPortHeight: Int = 0

    init {
        setupViews()
    }

    private fun onItemClick(position: Int) {
        Toast.makeText(context, "${position}", Toast.LENGTH_SHORT).show()
    }

    fun getScreenSizeInches(activity: Activity): Double {
        val windowManager = activity.windowManager
        val display = windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)

        // since SDK_INT = 1;
        var mWidthPixels = displayMetrics.widthPixels
        var mHeightPixels = displayMetrics.heightPixels

        // includes window decorations (statusbar bar/menu bar)
        // Note: this ignores current screen portait/landscape
        try {
            val realSize = Point()
            Display::class.java.getMethod("getRealSize", Point::class.java)
                .invoke(display, realSize)
            mWidthPixels = realSize.x
            mHeightPixels = realSize.y
        } catch (ignored: java.lang.Exception) {

        }


        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        val x = Math.pow(mWidthPixels / dm.xdpi.toDouble(), 2.0)
        val y = Math.pow(mHeightPixels / dm.ydpi.toDouble(), 2.0)
        return mWidthPixels / dm.xdpi.toDouble()// + y)
    }




    private fun setupViews()
    {
        val scr = context.toActivity().binding.framelayout
        //context.toActivity().binding.framelayout.invalidate()
    }
}