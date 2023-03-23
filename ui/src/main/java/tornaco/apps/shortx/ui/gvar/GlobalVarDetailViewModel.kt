package tornaco.apps.shortx.ui.gvar

import tornaco.apps.shortx.ui.base.StateViewModel

data class GlobalVarDetailState(
    val id: String = "",
    val name: String = "",
    val isChanged: Boolean = false
)

class GlobalVarDetailViewModel : StateViewModel<GlobalVarDetailState>() {
    override fun initState(): GlobalVarDetailState {
        return GlobalVarDetailState()
    }

    fun loadState(id: String) {
    }

    fun updateLabel(label: String) {
        updateState {
            copy(name = label, isChanged = true)
        }
    }

    fun save() {

    }
}