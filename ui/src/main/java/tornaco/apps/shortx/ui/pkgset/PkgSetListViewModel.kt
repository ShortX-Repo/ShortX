package tornaco.apps.shortx.ui.pkgset

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel
import java.util.*

data class PkgSetListState(
    val pkgSets: List<PkgSet> = emptyList()
)

class PkgSetListViewModel : StateViewModel<PkgSetListState>() {
    override fun initState(): PkgSetListState {
        return PkgSetListState()
    }

    fun loadState() {
        viewModelScope.launch(Dispatchers.IO) {
            val pkgSets = shortXManager.getAllPkgSets()
            viewModelScope.launch {
                updateState {
                    copy(pkgSets = pkgSets)
                }
            }
        }
    }

    fun createPkgSet(label: String) {
        shortXManager.addPkgSet(
            PkgSet.newBuilder().setLabel(label).setId(UUID.randomUUID().toString()).build()
        )
        loadState()
    }

}