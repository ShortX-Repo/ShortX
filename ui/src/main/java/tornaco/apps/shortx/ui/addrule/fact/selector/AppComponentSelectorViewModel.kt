package tornaco.apps.shortx.ui.addrule.fact.selector

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.util.ComponentRule
import tornaco.apps.shortx.ui.util.LCRulesAdapter
import java.util.*
import javax.inject.Inject

data class AppComponentInfoWithRule(
    val info: AppComponentInfo,
    val rule: ComponentRule?,
    val isRunning: Boolean = false
)

data class AppComponentStates(
    val activities: List<AppComponentInfoWithRule> = emptyList(),
    val services: List<AppComponentInfoWithRule> = emptyList(),
    val providers: List<AppComponentInfoWithRule> = emptyList(),
    val receivers: List<AppComponentInfoWithRule> = emptyList(),
    val keyword: String = "",
    val isLoading: Boolean = false
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AppComponentSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AppComponentStates>(context, { AppComponentStates() }) {

    fun updateKeyword(keyword: String) {
        updateState { copy(keyword = keyword) }
    }

    fun loadActivities(pkg: AppPkg) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val res = withContext(Dispatchers.IO) {
                shortXManager.getActivities(pkg)
                    .distinctBy { it.component }.map {
                        AppComponentInfoWithRule(
                            it,
                            kotlin.runCatching { LCRulesAdapter.getActivityRule(it.component.toComponentName()) }
                                .getOrNull()
                        )
                    }.filter(filterKeyword())
            }
            updateState {
                copy(activities = res, isLoading = false)
            }
        }
    }

    private fun filterKeyword(): (AppComponentInfoWithRule) -> Boolean {
        return {
            val kw = state.value.keyword
            kw.isBlank() || it.info.label.contains(
                kw,
                ignoreCase = true
            ) || it.info.component.className.contains(
                kw,
                ignoreCase = true
            ) || it.info.component.pkg.pkgName.contains(
                kw,
                ignoreCase = true
            ) || (it.rule?.label?.contains(kw, ignoreCase = true) ?: false)
        }
    }

    fun loadServices(pkg: AppPkg) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val res = withContext(Dispatchers.IO) {
                val runningService = shortXManager.getRunningServices(pkg)
                logger.d("runningService: $runningService")
                shortXManager.getServices(pkg).distinctBy { it.component }.map {
                    AppComponentInfoWithRule(
                        info = it,
                        rule = kotlin.runCatching { LCRulesAdapter.getServiceRule(it.component.toComponentName()) }
                            .getOrNull(),
                        isRunning = runningService.contains(it.component)
                    )
                }
            }.filter(filterKeyword())
            updateState {
                copy(services = res, isLoading = false)
            }
        }
    }

    fun loadProviders(pkg: AppPkg) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val res = withContext(Dispatchers.IO) {
                shortXManager.getProviders(pkg).distinctBy { it.component }.map {
                    AppComponentInfoWithRule(
                        it,
                        kotlin.runCatching { LCRulesAdapter.getProviderRule(it.component.toComponentName()) }
                            .getOrNull()
                    )
                }
            }.filter(filterKeyword())
            updateState {
                copy(providers = res, isLoading = false)
            }
        }
    }

    fun loadReceivers(pkg: AppPkg) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val res = withContext(Dispatchers.IO) {
                shortXManager.getReceivers(pkg).distinctBy { it.component }.map {
                    AppComponentInfoWithRule(
                        it,
                        kotlin.runCatching { LCRulesAdapter.getReceiverRule(it.component.toComponentName()) }
                            .getOrNull()
                    )
                }
            }.filter(filterKeyword())
            updateState {
                copy(receivers = res, isLoading = false)
            }
        }
    }
}