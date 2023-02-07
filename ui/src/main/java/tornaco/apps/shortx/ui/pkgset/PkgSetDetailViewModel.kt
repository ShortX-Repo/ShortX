package tornaco.apps.shortx.ui.pkgset

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel
import java.text.Collator
import java.util.*

data class PkgSetDetailState(
    val id: String = "",
    val label: String = "",
    val apps: List<App> = emptyList(),
    val selectableApps: List<App> = emptyList(),
    val installedApps: List<App> = emptyList(),
)

class PkgSetDetailViewModel : StateViewModel<PkgSetDetailState>() {
    override fun initState(): PkgSetDetailState {
        return PkgSetDetailState()
    }

    fun loadState(id: String) {
        viewModelScope.launch {
            shortXManager.getPkgSetById(id)?.let { pkgSet ->
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
                        id = pkgSet.id,
                        label = pkgSet.label,
                        apps = apps,
                        selectableApps = selectableApps,
                        installedApps = installedApps
                    )
                }
            }
        }
    }

    fun updateLabel(label: String) {
        updateState {
            copy(label = label)
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
                )
            }
        }
    }

}