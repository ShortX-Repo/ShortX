package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import tornaco.apps.shortx.core.cv.ICV
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.util.ExtAppClassHelper
import util.Reflections

class CVImpl(private val context: Context) : ICV.Stub() {
    private val logger = Logger("CVImpl")
    private val cvApiIns by lazy {
        ExtAppClassHelper.newInstance(
            context,
            "tornaco.apps.shortx.ui.cv.ShortXCVApi"
        )
    }

    override fun findImage(image: Bitmap, template: Bitmap): Point? {
        logger.d("cvApiIns: $cvApiIns")

        val point: Point? = Reflections.callMethod(cvApiIns, "findImage", image, template) as Point?

        logger.d("point: $point")
        return point
    }
}