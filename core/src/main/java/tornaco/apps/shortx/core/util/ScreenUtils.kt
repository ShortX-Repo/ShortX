package tornaco.apps.shortx.core.util

import android.content.Context
import android.graphics.Point
import android.view.Display
import tornaco.apps.shortx.core.util.windowManager

fun Context.screenSize(): Point {
    val wm = windowManager
    val display: Display = wm.defaultDisplay
    val point = Point()
    display.getRealSize(point)
    return point
}