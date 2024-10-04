package tornaco.apps.shortx.services34.input

import android.hardware.input.InputManagerGlobal
import android.view.InputEvent

object InputManagerGlobalV34 {
    fun injectInputEvent(event: InputEvent, injectMode: Int) {
        InputManagerGlobal.getInstance().injectInputEvent(event, injectMode)
    }
}