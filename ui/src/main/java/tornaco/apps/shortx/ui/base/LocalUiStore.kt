package tornaco.apps.shortx.ui.base

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.AtomicReference
import tornaco.apps.shortx.ui.addrule.action.model.Action

class UiStore {
    val ActionContent_CurrentAction: AtomicReference<Action?> = AtomicReference(null)
    val ShowOverlayButtonSelectorActivity_SelectButtonIcon_Index: AtomicReference<Int> =
        AtomicReference(0)
}

val LocalUiStore = staticCompositionLocalOf<UiStore> {
    error("LocalUiStore not provided")
}