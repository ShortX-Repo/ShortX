package tornaco.apps.shortx.ui.gvar

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel
import java.util.*

data class GlobalVarListState(
    val globalVars: List<GlobalVar> = emptyList()
)

class GlobalVarListViewModel : StateViewModel<GlobalVarListState>() {
    override fun initState(): GlobalVarListState {
        return GlobalVarListState()
    }

    fun loadState() {
        viewModelScope.launch(Dispatchers.IO) {
            val globalVars = shortXManager.getAllGlobalVars()
            viewModelScope.launch {
                updateState {
                    copy(globalVars = globalVars)
                }
            }
        }
    }

    fun createGlobalVar(name: String) {
        shortXManager.addGlobalVar(
            GlobalVar.newBuilder().setName(name).setId(UUID.randomUUID().toString()).build()
        )
        loadState()
    }

}