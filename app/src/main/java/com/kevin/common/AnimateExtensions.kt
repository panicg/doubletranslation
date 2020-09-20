package com.kevin.common

import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.util.Size
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

var View.center : Point
    get() {
        return Point((x + (width * 0.5f)).toInt(), (y + (height * 0.5f)).toInt())
    }
    set(value) {
        val width = width
        val height = height

        x = (value.x - (width * 0.5f))
        y = (value.y - (height * 0.5f))
    }

var View.centerF : PointF
    get() {
        return PointF(x + (width * 0.5f), y + (height * 0.5f))
    }
    set(value) {


        val width = width
        val height = height

        x = (value.x - (width * 0.5f))
        y = (value.y - (height * 0.5f))
    }




fun ViewPropertyAnimator.translationCenterF(x : Float, y : Float) : ViewPropertyAnimator {
    return translationX(x).translationY(y)
}

fun ViewPropertyAnimator.translationCenter(view : View, size : Size) : ViewPropertyAnimator {

    return translationX(size.width - (view.width * 0.5f))
        .translationY(size.height - (view.height * 0.5f))
}

fun ViewPropertyAnimator.center(view : View, point : Point) : ViewPropertyAnimator {
    return center(view, PointF(point))
}

fun ViewPropertyAnimator.center(view : View, point : PointF) : ViewPropertyAnimator {
    return x(point.x - (view.width * 0.5f))
        .y(point.y - (view.height * 0.5f))
}







