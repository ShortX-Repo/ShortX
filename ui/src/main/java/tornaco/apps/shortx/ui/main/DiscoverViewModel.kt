package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import tornaco.apps.shortx.ui.backup.BackupAgent
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject


data class DiscoverState(
    val rules: List<RuleUM> = emptyList(),
    val das: List<DirectActionUM> = emptyList(),
    val isLoading: Boolean = true
)

const val assetName = "prebuilt/prebuilt-all.zip"

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DiscoverModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<DiscoverState>(context, { DiscoverState() }) {
    init {
        viewModelScope.launch {
            loadPrebuiltDAs()
            loadPrebuiltRules()
        }
    }

    private suspend fun loadPrebuiltRules() {
        updateState { copy(isLoading = true) }
        kotlin.runCatching {
            val content = BackupAgent(context).readBackupPackageContent(assetName)
            val ruleUMs = withContext(Dispatchers.IO) {
                content.rules.map {
                    ruleToRuleUM(it, emptyList())
                }
            }
            updateState { copy(rules = ruleUMs, isLoading = false) }
        }.onFailure {
            logger.e(it, "loadPrebuiltRules error")
            updateState { copy(isLoading = false) }
        }
    }

    private suspend fun loadPrebuiltDAs() {
        updateState { copy(isLoading = true) }
        kotlin.runCatching {
            val content = BackupAgent(context).readBackupPackageContent(assetName)
            val daUMs = withContext(Dispatchers.IO) {
                content.das.map {
                    daToDirectActionUM(it, emptyList())
                }
            }
            updateState { copy(das = daUMs, isLoading = false) }
        }.onFailure {
            logger.e(it, "loadPrebuiltDAs error")
            updateState { copy(isLoading = false) }
        }
    }
}