package tornaco.apps.shortx.ui.overlay

import tornaco.apps.shortx.ui.base.StateViewModel

object OverlayState

class OverlayViewModel : StateViewModel<OverlayState>() {
    fun start() {
        loadState()
    }

    private fun loadState() {

    }

    override fun initState() = OverlayState
}