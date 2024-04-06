package tornaco.apps.shortx.ui.addrule.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import java.text.Collator
import java.util.*
import javax.inject.Inject

fun List<AppWithState>.filterSearchKeyword(keyword: String?): List<AppWithState> {
    return if (keyword.isNullOrBlank()) this else filter {
        it.app.label.contains(keyword, ignoreCase = true)
                || it.app.pkg.pkgName.contains(keyword, ignoreCase = true)
    }
}

data class AppWithState(val app: App, val isRunning: Boolean)

data class AppStates(
    val installedApps: List<AppWithState> = emptyList(),
    val isLoading: Boolean = false
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AppSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<AppStates>(context, { AppStates() }) {

    fun loadInstalledApps(pkgSetLabel: String? = null) {
        viewModelScope.launch {
            val runningApps = withContext(Dispatchers.IO) {
                shortXManager.getRunningAppPkgs("All")
            }
            val pkgSetApps = pkgSetLabel?.let {
                shortXManager.getPkgSetByLabel(it)
            }

            val apps = withContext(Dispatchers.IO) {
                shortXManager.getInstalledApps()
                    .filter {
                        pkgSetApps == null || pkgSetApps.appPkgsList.contains(it.pkg)
                    }
                    .sortedWith { o1, o2 ->
                        Collator.getInstance(Locale.CHINESE).compare(o1.label, o2.label)
                    }.sortedBy { it.category.ordinal }
            }.map {
                AppWithState(app = it, isRunning = runningApps.contains(it.pkg))
            }
            updateState {
                copy(installedApps = apps)
            }
        }
    }
}