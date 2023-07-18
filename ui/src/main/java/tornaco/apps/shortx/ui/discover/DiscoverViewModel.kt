package tornaco.apps.shortx.ui.discover

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addda.defaultNewDAId
import tornaco.apps.shortx.ui.backup.BackupAgent
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.main.DirectActionUM
import tornaco.apps.shortx.ui.main.RuleUM
import tornaco.apps.shortx.ui.main.daToDirectActionUM
import tornaco.apps.shortx.ui.main.ruleToRuleUM
import tornaco.apps.shortx.ui.remote.OnlineRepo
import java.util.*
import javax.inject.Inject

sealed interface DiscoverRepo {
    object PrebuiltLocal : DiscoverRepo
    object OfficialOnline : DiscoverRepo
    data class CustomOnline(val alias: String, val indexUrl: String) : DiscoverRepo
}

data class DiscoverState(
    val rules: List<RuleUM> = emptyList(),
    val das: List<DirectActionUM> = emptyList(),
    val discoverRepo: DiscoverRepo = DiscoverRepo.PrebuiltLocal,
    val isLoading: Boolean = true
)

const val assetName = "prebuilt/prebuilt-all.zip"

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DiscoverViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<DiscoverState>(context, { DiscoverState() }) {
    private val onlineRepo = OnlineRepo()

    fun switchRepo(repo: DiscoverRepo) {
        updateState { copy(discoverRepo = repo, das = emptyList(), rules = emptyList()) }
        viewModelScope.launch { loadDiscoverData() }
    }

    fun loadDiscoverData() {
        viewModelScope.launch {
            when (state.value.discoverRepo) {
                is DiscoverRepo.CustomOnline -> {
                    loadOnlineDAs()
                }

                DiscoverRepo.OfficialOnline -> {
                    loadOnlineDAs()
                }

                DiscoverRepo.PrebuiltLocal -> {
                    loadPrebuiltDAs()
                    loadPrebuiltRules()
                }
            }
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
                content.das.map { directAction ->
                    daToDirectActionUM(directAction, emptyList())
                }
            }
            updateState { copy(das = daUMs, isLoading = false) }
        }.onFailure {
            logger.e(it, "loadPrebuiltDAs error")
            updateState { copy(isLoading = false) }
        }
    }

    private suspend fun loadOnlineDAs() {
        updateState { copy(isLoading = true) }
        kotlin.runCatching {
            val index = onlineRepo.index()
            val das = index.directActions.map {
                DirectActionUM(
                    title = it.title,
                    description = it.description,
                    createTime = "",
                    updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(System.currentTimeMillis())
                    ),
                    icon = it.icon ?: Remix.Development.command_line,
                    iconColor = Color.Red.value,
                    id = defaultNewDAId(),
                    runningInsCount = 0,
                    directAction = DirectAction.newBuilder().setId(it.fileUrl).build()
                )
            }
            updateState { copy(das = das, isLoading = false) }
        }.onFailure {
            logger.e(it, "loadOnlineDAs error")
            updateState { copy(isLoading = false) }
        }
    }
}