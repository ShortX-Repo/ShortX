package tornaco.apps.shortx.services34.wm

import android.view.IWindowManager

object WindowManagerV34 {
    fun IWindowManager.doThawRotation() {
        thawRotation()
    }
}