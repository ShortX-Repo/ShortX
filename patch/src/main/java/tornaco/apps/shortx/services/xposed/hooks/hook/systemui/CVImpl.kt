package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.Rect
import tornaco.apps.shortx.core.cv.ICV
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.util.ExtAppClassHelper
import util.Reflections

class CVImpl(private val context: Context) : ICV.Stub() {
    private val logger = Logger("CVImpl")
    private val cvApiIns by lazy {
        ExtAppClassHelper.newInstance(
            context,
            "tornaco.apps.shortx.ext.api.cv.ShortXCVApi"
        )
    }

    override fun findImage(image: Bitmap, template: Bitmap): Point? {
        logger.d("findImage cvApiIns: $cvApiIns")
        val point: Point? = Reflections.callMethod(cvApiIns, "findImage", image, template) as Point?
        logger.d("findImage point: $point")
        return point
    }

    override fun findPointsByColor(
        image: Bitmap,
        color: Int,
        threshold: Int,
        rect: Rect?
    ): List<Point> {
        logger.d("findPointsByColor cvApiIns: $cvApiIns")
        val res =
            Reflections.callMethod(cvApiIns, "findPointsByColor", image, color, threshold, rect)
        logger.d("findPointsByColor points: $res")
        @Suppress("UNCHECKED_CAST")
        val points = (res as List<Point>)
        return points.take(1)
    }
}