package tornaco.apps.shortx.services.app

import android.view.Display

data class InDisplay<T>(val content: T, val displayId: Int)

val InDisplay<*>.isInDefaultDisplay get() = displayId == Display.DEFAULT_DISPLAY

fun <T> T.inDisplay(displayId: Int): InDisplay<T> {
    return InDisplay(this, displayId)
}
