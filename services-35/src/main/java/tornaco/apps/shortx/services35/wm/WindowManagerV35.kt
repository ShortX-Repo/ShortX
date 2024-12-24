package tornaco.apps.shortx.services35.wm

import android.view.IWindowManager

object WindowManagerV35 {
    fun IWindowManager.doThawRotation() {
        thawRotation("ShortX")
    }
}