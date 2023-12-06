package io.ak1.drawbox

import android.view.View
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

/**
 * Created by akshay on 24/12/21
 * https://ak1.io
 */


private fun View.getRect(x: Int, y: Int): android.graphics.Rect {
    val viewWidth = this.width
    val viewHeight = this.height
    return android.graphics.Rect(x, y, viewWidth + x, viewHeight + y)
}


//Model
data class PathWrapper(
    var points: SnapshotStateList<Offset>,
    val strokeWidth: Float = 5f,
    val strokeColor: Color,
    val alpha: Float = 1f
)

data class DrawBoxPayLoad(val bgColor: Color, val path: List<PathWrapper>)

fun createPath(points: List<Offset>) = Path().apply {
    if (points.size > 1) {
        var oldPoint: Offset? = null
        this.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            val point: Offset = points[i]
            oldPoint?.let {
                val midPoint = calculateMidpoint(it, point)
                if (i == 1) {
                    this.lineTo(midPoint.x, midPoint.y)
                } else {
                    this.quadraticBezierTo(it.x, it.y, midPoint.x, midPoint.y)
                }
            }
            oldPoint = point
        }
        oldPoint?.let { this.lineTo(it.x, oldPoint.y) }
    }
}

private fun calculateMidpoint(start: Offset, end: Offset) =
    Offset((start.x + end.x) / 2, (start.y + end.y) / 2)

