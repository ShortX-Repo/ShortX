package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import tornaco.apps.shortx.core.proto.fact.ScreenCorner

object CornerSwipeToCenterGeometry {
    const val DEFAULT_CORNER_REGION_RADIUS_RATIO = 0.18f

    fun radius(
        width: Float,
        height: Float,
        radiusRatio: Float = DEFAULT_CORNER_REGION_RADIUS_RATIO
    ): Float {
        return minOf(width, height) * radiusRatio
    }

    fun contains(
        corner: ScreenCorner,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        radiusRatio: Float = DEFAULT_CORNER_REGION_RADIUS_RATIO
    ): Boolean {
        val radius = radius(width, height, radiusRatio)
        val radiusSquare = radius * radius
        val (cornerX, cornerY) = cornerPoint(corner, width, height)
        val dx = x - cornerX
        val dy = y - cornerY
        return dx * dx + dy * dy <= radiusSquare
    }

    fun cornerPoint(corner: ScreenCorner, width: Float, height: Float): Pair<Float, Float> {
        return when (corner) {
            ScreenCorner.CornerTopLeft -> 0f to 0f
            ScreenCorner.CornerTopRight -> width to 0f
            ScreenCorner.CornerBottomLeft -> 0f to height
            ScreenCorner.CornerBottomRight -> width to height
            else -> 0f to 0f
        }
    }
}