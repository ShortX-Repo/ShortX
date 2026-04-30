package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.view.InputEvent

interface InputInjectionBridge {
    fun injectEvent(event: InputEvent)
    fun setFilterEnabled(enabled: Boolean)
}
