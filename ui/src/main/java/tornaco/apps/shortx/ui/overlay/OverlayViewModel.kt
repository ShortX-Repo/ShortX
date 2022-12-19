package tornaco.apps.shortx.ui.overlay

import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.ui.base.StateViewModel

data class OverlayState(
    val buttonIcons: List<String>,
    val enabledButtons: Set<Int>
)

class OverlayViewModel : StateViewModel<OverlayState>() {
    fun start() {
        loadState()
    }

    override fun onCleared() {
        super.onCleared()
    }

    private fun loadState() {

    }

    override fun initState() = OverlayState(
        buttonIcons = mutableListOf<String>().apply {
            repeat(BUTTON_COUNT) {
                add(BuildProp.DEFAULT_OVERLAY_BUTTON_ICON)
            }
        },
        enabledButtons = emptySet()
    )

    fun onButtonClick(index: Int) {

    }
}