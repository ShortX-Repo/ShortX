package tornaco.apps.shortx.ui.pkgset

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.IPkgSetObserver
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.InputValidateResult.OK.toError
import tornaco.apps.shortx.ui.base.InputValidateResult.OK.toWarn
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.afterNavDelay

data class PkgSetListState(
    val pkgSets: List<PkgSet> = emptyList(),
    val isLoading: Boolean = false
)

class PkgSetListViewModel : StateViewModel<PkgSetListState, Unit>({ PkgSetListState() }) {
    private val obs = object : IPkgSetObserver.Stub() {
        override fun onAddOrUpdate(id: String?) {
            loadState()
        }

        override fun onDelete(id: String?) {
            loadState()
        }
    }

    init {
        shortXManager.registerPkgSetObs(obs)
    }

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

    fun validateLabel(
        label: String,
        i18N: I18N,
        errorIfExists: Boolean = true
    ): InputValidateResult {
        return if (label.isBlank()) {
            "".toError()
        } else if (shortXManager.getPkgSetByLabel(label.trim()) != null) {
            errorIfExists.select(
                i18N["ui.pkg.set.add.input.label.exists"].toError(),
                i18N["ui.pkg.set.add.input.label.exists"].toWarn()
            )
        } else {
            InputValidateResult.OK
        }
    }

    fun createPkgSet(label: String, description: String) {
        shortXManager.addPkgSet(
            PkgSet.newBuilder()
                .setLabel(label.trim())
                .setDescription(description.trim())
                .build()
        )
        loadState()
    }

    fun delete(label: String) {
        shortXManager.deletePkgSet(label)
        loadState()
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterPkgSetObs(obs)
    }
}