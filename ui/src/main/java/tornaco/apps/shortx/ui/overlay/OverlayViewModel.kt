package tornaco.apps.shortx.ui.overlay

import android.content.Context
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel

data class OverlayState(
    val buttonIcons: List<String>
)

class OverlayViewModel : StateViewModel<OverlayState>() {
    fun start(context: Context) {
        loadState(context)


    }

    private fun loadState(context: Context) {
        updateState {
            copy(
                buttonIcons = mutableListOf<String>().apply {
                    repeat(BUTTON_COUNT) { index ->
                        val name = context.shortXManager.getOverlayButtonIconName(index)
                        add(name)
                    }
                }
            )
        }
    }

    override fun initState() = OverlayState(mutableListOf<String>().apply {
        repeat(BUTTON_COUNT) {
            add(BuildProp.DEFAULT_OVERLAY_BUTTON_ICON)
        }
    })
}