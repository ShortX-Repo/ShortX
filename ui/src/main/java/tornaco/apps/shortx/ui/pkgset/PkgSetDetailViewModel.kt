package tornaco.apps.shortx.ui.pkgset

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.afterNavDelay
import java.text.Collator
import java.util.Locale

data class PkgSetDetailState(
    val originalLabel: String = "",
    val label: String = "",
    val description: String = "",
    val apps: List<App> = emptyList(),
    val selectableApps: List<App> = emptyList(),
    val installedApps: List<App> = emptyList(),
    val isPrebuilt: Boolean = false,
    val isEditable: Boolean = false,
    val isChanged: Boolean = false,
    val isLoading: Boolean = true
)

class PkgSetDetailViewModel : StateViewModel<PkgSetDetailState>({ PkgSetDetailState() }) {

    fun loadState(label: String) {
        updateState { copy(originalLabel = label) }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateState { copy(isLoading = true) }

                afterNavDelay()

                shortXManager.getPkgSetByLabel(label)?.let { pkgSet ->
                    val apps = pkgSet.appPkgsList.mapNotNull { pkg ->
                        shortXManager.pkgToApp(pkg)
                    }

                    val installedApps = shortXManager.getInstalledApps()
                        .sortedWith { o1, o2 ->
                            Collator.getInstance(Locale.CHINESE).compare(o1.label, o2.label)
                        }.sortedBy { it.category.ordinal }

                    val selectableApps = installedApps.toMutableList().apply {
                        removeAll(apps)
                    }

                    updateState {
                        copy(
                            label = pkgSet.label,
                            description = pkgSet.description,
                            apps = apps,
                            selectableApps = selectableApps,
                            installedApps = installedApps,
                            isPrebuilt = pkgSet.isPrebuilt,
                            isEditable = pkgSet.isEditable,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun updateLabel(label: String) {
        updateState {
            copy(label = label, isChanged = true)
        }
    }

    fun addApps(apps: List<App>) {
        viewModelScope.launch {
            val newApps = state.value.apps.toMutableList().apply {
                addAll(apps)
            }
            val selectableApps = state.value.installedApps.toMutableList().apply {
                removeAll(newApps)
            }
            updateState {
                copy(
                    apps = newApps,
                    selectableApps = selectableApps,
                    isChanged = true
                )
            }
        }
    }

    fun removeApps(apps: List<App>) {
        viewModelScope.launch {
            val newApps = state.value.apps.toMutableList().apply {
                removeAll(apps)
            }
            val selectableApps = state.value.installedApps.toMutableList().apply {
                removeAll(newApps)
            }
            updateState {
                copy(
                    apps = newApps,
                    selectableApps = selectableApps,
                    isChanged = true
                )
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            with(state.value) {
                if (isChanged) {
                    val pkgs: List<AppPkg> = apps.map {
                        it.pkg
                    }
                    shortXManager.deletePkgSet(originalLabel)
                    shortXManager.addPkgSet(
                        PkgSet
                            .newBuilder()
                            .setDescription(description)
                            .setLabel(label)
                            .addAllAppPkgs(pkgs)
                            .build()
                    )
                }
            }
        }
    }
}