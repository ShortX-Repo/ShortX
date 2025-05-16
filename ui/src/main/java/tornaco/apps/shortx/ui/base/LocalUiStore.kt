package tornaco.apps.shortx.ui.base

import androidx.compose.runtime.staticCompositionLocalOf
import tornaco.apps.shortx.ui.addrule.action.model.Action
import java.util.concurrent.atomic.AtomicReference

class UiStore {
    val ActionContent_CurrentAction: AtomicReference<Action?> = AtomicReference(null)
    val ShowOverlayButtonSelectorActivity_SelectButtonIcon_Index: AtomicReference<Int> =
        AtomicReference(0)
}

val LocalUiStore = staticCompositionLocalOf<UiStore> {
    error("LocalUiStore not provided")
}