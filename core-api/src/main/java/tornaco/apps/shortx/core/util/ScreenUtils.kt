package tornaco.apps.shortx.core.util

import android.content.Context
import android.graphics.Point
import android.os.ServiceManager
import android.view.Display
import android.view.IWindowManager

private val logger = Logger("ScreenUtils")

fun Context.screenSize(): Point {
    return kotlin.runCatching {
        val point = Point()
        IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE))
            .getBaseDisplaySize(displayIdCompat, point)
        logger.d("getBaseDisplaySize: $point")
        point
    }.getOrElse { throwable ->
        logger.e(throwable, "ERROR getting screen size")
        screenSizeFallback().also {
            logger.d("screenSizeFallback: $it")
        }
    }
}

private fun Context.screenSizeFallback(): Point {
    val display = defaultDisplay()
    val point = Point()
    display.getRealSize(point)
    return point
}

fun Context.defaultDisplay(): Display {
    return if (OsUtils.isROrAbove()) {
        runCatching {
            // When running in app process
            // java.lang.UnsupportedOperationException: Tried to obtain display from a Context
            // not associated with one. Only visual Contexts (such as Activity or one created with Context#createWindowContext)
            // or ones created with Context#createDisplayContext are associated with displays.
            // Other types of Contexts are typically related to background entities and may return an arbitrary display.
            // 02-02 16:03:52.736 25920 25973 E AndroidRuntime: 	at android.app.ContextImpl.getDisplay(ContextImpl.java:3036)
            this.display
        }.getOrElse {
            val wm = windowManager
            wm.defaultDisplay
        }
    } else {
        val wm = windowManager
        return wm.defaultDisplay
    }
}

val Context.displayIdCompat: Int
    get() = OsUtils.isSOrAbove().select(
        t = { displayId },
        f = { Display.DEFAULT_DISPLAY }
    )