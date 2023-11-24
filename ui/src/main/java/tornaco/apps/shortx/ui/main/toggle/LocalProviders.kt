package tornaco.apps.shortx.ui.main.toggle

import androidx.compose.runtime.staticCompositionLocalOf
import tornaco.apps.shortx.ui.main.model.ToggleUM

interface ToggleItemHandler {
    fun run(toggle: ToggleUM)
    fun dup(toggle: ToggleUM)
    fun delete(toggle: ToggleUM)
    fun edit(toggle: ToggleUM)
    fun click(toggle: ToggleUM)
    fun share(toggle: ToggleUM)
    fun moveToSet(toggle: ToggleUM, setId: String)
    fun moveOutSet(toggle: ToggleUM)
    fun stopAllJobs(toggle: ToggleUM)
    fun setToggleEnabled(toggle: ToggleUM, enable: Boolean)
}

interface ToggleSetItemHandler {
    fun dismiss(toggle: ToggleUM) {}
    fun delete(toggle: ToggleUM, alsoDeleteItems: Boolean) {}
    fun rename(toggle: ToggleUM, newName: String) {}
    fun click(toggle: ToggleUM) {}
    fun share(toggle: ToggleUM) {}

    // Batch op
    fun setToggleEnabled(toggle: ToggleUM, enable: Boolean) {}
}

val LocalToggleItemHandler = staticCompositionLocalOf<ToggleItemHandler> {
    error("CompositionLocal LocalToggleItemHandler not present")
}
val LocalToggleSetItemHandler = staticCompositionLocalOf<ToggleSetItemHandler> {
    error("CompositionLocal ToggleSetItemHandler not present")
}