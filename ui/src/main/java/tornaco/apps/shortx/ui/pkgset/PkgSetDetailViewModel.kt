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
    val installedApps: List<App> = emptyList(),
    val isPrebuilt: Boolean = false,
    val autoAddNewlyInstalledApps: Boolean = false,
    val isChanged: Boolean = false,
    val isLoading: Boolean = true
)

class PkgSetDetailViewModel : StateViewModel<PkgSetDetailState, Unit>({ PkgSetDetailState() }) {

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
                    updateState {
                        copy(
                            label = pkgSet.label,
                            description = pkgSet.description,
                            apps = apps,
                            installedApps = installedApps,
                            isPrebuilt = pkgSet.isPrebuilt,
                            autoAddNewlyInstalledApps = pkgSet.autoAddNewlyInstalledApps,
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

    fun updateAutoAddNewlyInstalledApps(autoAdd: Boolean) {
        updateState {
            copy(autoAddNewlyInstalledApps = autoAdd, isChanged = true)
        }
    }

    fun setApps(apps: List<App>) {
        viewModelScope.launch {
            updateState {
                copy(
                    apps = apps,
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
            updateState {
                copy(
                    apps = newApps,
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
                            .setAutoAddNewlyInstalledApps(autoAddNewlyInstalledApps)
                            .build()
                    )
                }
            }
        }
    }
}