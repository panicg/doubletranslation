package com.panic.doubletranslation.view.base

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.kevin.common.CommonApplication

class ProgressView(context: Context) : FrameLayout(context) {

//    val resImage: List<Int> = listOf(
//        R.drawable.slot_1,
//        R.drawable.slot_2,
//        R.drawable.slot_3,
//        R.drawable.slot_4,
//        R.drawable.slot_5,
//        R.drawable.slot_6,
//        R.drawable.slot_7,
//        R.drawable.slot_8,
//        R.drawable.slot_9,
//        R.drawable.slot_10,
//        R.drawable.slot_11,
//        R.drawable.slot_12
//    )

    private val progressBar : ProgressBar

//    private val imageView: ImageView
//    private val animatedDrawable: AnimationDrawable

    private val h = Handler(Looper.getMainLooper())

    init {

        setBackgroundColor(Color.WHITE)

//        imageView = ImageView(context).apply {
//            layoutParams =
//                LayoutParams(AndroidUtilities.dp(100f), AndroidUtilities.dp(100f)).apply {
//                    gravity = Gravity.CENTER
//                }
//        }
//
//        animatedDrawable = AnimationDrawable().apply {
//            repeat(resImage.count()) {
//                addFrame(context.getDrawable(resImage[it])!!, 150)
//            }
//            isOneShot = false
//        }
//
//        imageView.background = animatedDrawable

        progressBar = ProgressBar(CommonApplication.appContext)

//        addView(imageView)
        addView(progressBar)

    }

    var isProgressAnimated = false

    fun show(parent: ViewGroup, backgroundColor : Int = Color.WHITE) {
        if (this.parent != null) return
        h.post {

            this@ProgressView.setBackgroundColor(backgroundColor)
            this.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            parent.addView(this)

//            animatedDrawable.start()

            this.animate().cancel()
            this.animate().alpha(1f).setDuration(100).withStartAction {
                this@ProgressView.alpha = 0f
            }.withEndAction { }


        }
    }

    fun hide() {
        h.post {
            this.animate().cancel()
            this.animate().alpha(0f).setDuration(100).withEndAction {
//                animatedDrawable.stop()
                (this.parent as? ViewGroup)?.removeView(this)
            }

        }

    }

}