package tornaco.apps.shortx.core.util

import android.content.Context
import android.graphics.Point
import android.view.Display

fun Context.screenSize(): Point {
    val display = defaultDisplay()
    val point = Point()
    display.getRealSize(point)
    return point
}

fun Context.defaultDisplay(): Display {
    return if (OsUtils.isROrAbove()) {
        this.display
    } else {
        val wm = windowManager
        return wm.defaultDisplay
    }
}