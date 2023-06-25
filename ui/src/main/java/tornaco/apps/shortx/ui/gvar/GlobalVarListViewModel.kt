package tornaco.apps.shortx.ui.gvar

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel

data class GlobalVarListState(
    val globalVars: List<GlobalVar> = emptyList()
)

class GlobalVarListViewModel : StateViewModel<GlobalVarListState>({ GlobalVarListState() }) {
    private val globalVarObs = object : IGlobalVarObserver.Stub() {
        override fun onAddOrUpdate(id: String?) {
            loadState()
        }

        override fun onDelete(id: String?) {
            loadState()
        }
    }

    init {
        shortXManager.registerGlobalVarObs(globalVarObs)
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

    fun createGlobalVar(globalVar: GlobalVar) {
        shortXManager.addGlobalVar(globalVar)
        loadState()
    }

    fun delete(name: String) {
        viewModelScope.launch {
            shortXManager.deleteGlobalVar(name)
            loadState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterGlobalVarObs(globalVarObs)
    }
}