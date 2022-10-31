package tornaco.apps.shortx.services

import android.content.Context
import android.graphics.Point
import android.view.Display

fun Context.screenSize(): Point {
    val wm = windowManager
    val display: Display = wm.defaultDisplay
    val point = Point()
    display.getRealSize(point)
    return point
}