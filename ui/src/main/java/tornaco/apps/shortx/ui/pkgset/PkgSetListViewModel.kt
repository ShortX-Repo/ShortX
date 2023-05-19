package tornaco.apps.shortx.ui.pkgset

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.afterNavDelay

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

    fun validateLabel(label: String, i18N: I18N): Pair<Boolean, String> {
        return if (label.isBlank()) false to ""
        else if (shortXManager.getPkgSetByLabel(label.trim()) != null) false to i18N["ui.pkg.set.add.input.label.exists"]
        else true to ""
    }

    fun createPkgSet(label: String) {
        shortXManager.addPkgSet(
            PkgSet.newBuilder().setLabel(label).build()
        )
        loadState()
    }

}