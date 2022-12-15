package tornaco.apps.shortx.ui.overlay

import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.overlay.OverlayButtonSettingsChangeListener
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel

data class OverlayState(
    val buttonIcons: List<String>
)

class OverlayViewModel : StateViewModel<OverlayState>() {
    private val overlayChangeListener = object : OverlayButtonSettingsChangeListener() {
        override fun postOverlayButtonSettingsChanged(index: Int) {
            loadState()
        }
    }

    fun start() {
        shortXManager.registerOverlayButtonSettingsChangeListener(overlayChangeListener)
        loadState()
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unRegisterOverlayButtonSettingsChangeListener(overlayChangeListener)
    }

    private fun loadState() {
        updateState {
            copy(buttonIcons = mutableListOf<String>().apply {
                repeat(BUTTON_COUNT) { index ->
                    val name = shortXManager.getOverlayButtonIconName(index)
                    add(name)
                }
            })
        }
    }

    override fun initState() = OverlayState(mutableListOf<String>().apply {
        repeat(BUTTON_COUNT) {
            add(BuildProp.DEFAULT_OVERLAY_BUTTON_ICON)
        }
    })
}