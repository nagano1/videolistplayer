package org.rokist.videolistplayer.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

fun maybeNo(context: Context) {
    val tv = TextView(context)
    tv.setId(View.generateViewId())

    val rl = RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        33
    )
    rl.addRule(RelativeLayout.CENTER_VERTICAL, tv.id)
    //scrContentRelativeLayout.addView(tv, rl)
}


/*

 */

/*
private fun replaceWithCapturedImage(itemView: ItemViewHolder) {
    itemView.hiddenLinearLayout?.let {
        val observer = it.viewTreeObserver
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                it.viewTreeObserver.removeOnGlobalLayoutListener(this)

                if (itemView.linearLayout.parent != null && itemView.deleted == false) {
                    getBitmapFromView(it)?.let { bitmap ->
                        val v = ImageView(context)
                        v.setImageBitmap(bitmap)
                        v.layoutParams = LinearLayout.LayoutParams(
                            it.width,
                            it.height
                        )

                        (it.parent as ViewGroup).removeView(itemView.hiddenLinearLayout)

                        //itemView.linearLayout.removeAllViews()
                        itemView.linearLayout.addView(v)
                        itemView.imageView = v
//                        itemView.linearLayout.visibility = View.VISIBLE
                    }
                } else {
                    //Log.d("aaa", "repalce deleted ${itemView.idx}")
                }
            }
        }
        observer.addOnGlobalLayoutListener(listener)
    }
}
 */


fun getBitmapFromView(view: View): Bitmap? {
    if (view.width > 0 && view.height > 0) {

        val returnedBitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        /*
    val bgDrawable = view.background
    if (bgDrawable != null)
        bgDrawable.draw(canvas)
    else
        canvas.drawColor(Color.WHITE)
     */
        view.draw(canvas)
        return returnedBitmap
    }
    return null
}

/*
val inBackground = false//inBackground2
if (inBackground) {
/*
            val imageLinearLayout = LinearLayout(context).also {
                if (false && rr < 0.5) {
                    it.setBackgroundColor(
                        if (i % 2 == 0) Color.parseColor("#F7F7F7")
                        else Color.parseColor("#FFFFFF")
                    )
                } else {
                    it.setBackgroundColor(if (i % 2 == 0) Color.GREEN else Color.YELLOW)
/*
                it.setBackgroundColor(
                    if (i % 2 == 0) Color.parseColor("#F7F7F7")
                    else Color.parseColor("#FFFFFF")
                )
 */
                }

                val rl = FrameLayout.LayoutParams(
                    calcModel.panelWidth,
                    viewSettings.lineHeight
                )
                it.layoutParams = rl

                itemView.hiddenLinearLayout = it

                itemView.hiddenLinearLayout?.let {
                    generateInnerView(itemView, it)
                }


                hiddenFrameLayout.addView(it)
            }

            itemView.hiddenLinearLayout = imageLinearLayout
 */

}
 */