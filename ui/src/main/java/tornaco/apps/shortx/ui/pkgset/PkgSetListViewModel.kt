package tornaco.apps.shortx.ui.pkgset

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.afterNavDelay
import java.util.*

data class PkgSetListState(
    val pkgSets: List<PkgSet> = emptyList(),
    val isLoading: Boolean = false
)

class PkgSetListViewModel : StateViewModel<PkgSetListState>(PkgSetListState()) {

    fun loadState() {
        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(isLoading = true) }
            afterNavDelay()

            val pkgSets = shortXManager.getAllPkgSets()
            updateState {
                copy(pkgSets = pkgSets, isLoading = false)
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