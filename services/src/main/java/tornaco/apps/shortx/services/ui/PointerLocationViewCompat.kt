package tornaco.apps.shortx.services.ui

import android.content.Context
import android.view.MotionEvent
import android.view.View
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ui.v34.PointerLocationViewImplV34

class PointerLocationViewCompat(val context: Context) {
    private val logger = Logger("PointerLocationViewCompat")

    val view: PointerLocationView by lazy {
        if (OsUtils.isUOrAbove()) {
            logger.i("Init PointerLocationViewImplV34")
            PointerLocationViewImplV34(context)
        } else {
            logger.i("Init PointerLocationViewImpl")
            PointerLocationViewImpl(context)
        }
    }

    fun onPointerEvent(event: MotionEvent) {
        view.onPointerEvent(event)
    }

    fun setPrintCoords(b: Boolean) {
        view.setPrintCoords(b)
    }
}

abstract class PointerLocationView(context: Context) : View(context) {
    abstract fun onPointerEvent(event: MotionEvent)

    abstract fun setPrintCoords(b: Boolean)
}