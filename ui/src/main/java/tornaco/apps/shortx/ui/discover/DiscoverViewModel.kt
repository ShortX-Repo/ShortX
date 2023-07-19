package tornaco.apps.shortx.ui.discover

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.BuildProp.SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addda.defaultNewDAId
import tornaco.apps.shortx.ui.backup.BackupAgent
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.base.colorFromHex
import tornaco.apps.shortx.ui.main.DirectActionUM
import tornaco.apps.shortx.ui.main.RuleUM
import tornaco.apps.shortx.ui.main.daToDirectActionUM
import tornaco.apps.shortx.ui.main.ruleToRuleUM
import tornaco.apps.shortx.ui.remote.OnlineRepo
import tornaco.apps.shortx.ui.settings.pref.DiscoveryRepoSetting
import tornaco.apps.shortx.ui.settings.pref.Pref
import java.util.*
import javax.inject.Inject

sealed interface DiscoverRepo {
    object PrebuiltLocal : DiscoverRepo
    object OfficialOnline : DiscoverRepo
    data class CustomOnline(val indexUrl: String) : DiscoverRepo
}

data class DiscoverState(
    val rules: List<RuleUM> = emptyList(),
    val das: List<DirectActionUM> = emptyList(),
    val discoverRepo: DiscoverRepo = DiscoverRepo.PrebuiltLocal,
    val customRepos: Set<DiscoveryRepoSetting> = emptySet(),
    val isLoading: Boolean = true
)

const val assetName = "prebuilt/prebuilt-all.zip"

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DiscoverViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<DiscoverState>(context, { DiscoverState() }) {
    private val onlineRepo = OnlineRepo()
    private val pref by lazy { Pref(context) }

    init {
        viewModelScope.launch {
            pref.discoveryRepos.collectLatest {
                logger.d("discoveryRepos updated: $it")
                updateState {
                    copy(
                        customRepos = it
                    )
                }
            }
        }

        viewModelScope.launch {
            pref.discoverySelectedRepoUrl.collectLatest {
                logger.d("discoverySelectedRepoUrl updated: $it")
                val repo = when (it) {
                    "" -> {
                        DiscoverRepo.PrebuiltLocal
                    }

                    SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL -> {
                        DiscoverRepo.OfficialOnline
                    }

                    else -> {
                        DiscoverRepo.CustomOnline(it)
                    }
                }
                switchRepo(repo)
            }
        }
    }

    fun addCustomRepo(setting: DiscoveryRepoSetting) {
        viewModelScope.launch {
            pref.addDiscoveryRepo(setting)
        }
    }

    fun removeCustomRepo(setting: DiscoveryRepoSetting) {
        viewModelScope.launch {
            pref.removeDiscoveryRepo(setting)
        }
    }

    fun switchRepo(repo: DiscoverRepo) {
        updateState { copy(discoverRepo = repo, das = emptyList(), rules = emptyList()) }
        viewModelScope.launch {
            val url = if (repo is DiscoverRepo.CustomOnline) {
                repo.indexUrl
            } else if (repo == DiscoverRepo.OfficialOnline) {
                SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL
            } else ""
            pref.selectRepo(url)
            loadDiscoverData()
        }
    }

    fun loadDiscoverData() {
        viewModelScope.launch {
            when (val repo = state.value.discoverRepo) {
                is DiscoverRepo.CustomOnline -> {
                    loadOnlineDAs(repo.indexUrl)
                }

                DiscoverRepo.OfficialOnline -> {
                    loadOnlineDAs(SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL)
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

    private suspend fun loadOnlineDAs(url: String) {
        updateState { copy(isLoading = true) }
        kotlin.runCatching {
            val index = onlineRepo.index(url)
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
                    iconColor = it.iconColor?.let { it1 -> colorFromHex(it1) }?.value
                        ?: Color.Magenta.value,
                    id = defaultNewDAId(),
                    runningInsCount = 0,
                    directAction = DirectAction.newBuilder().setId(it.fileUrl).build(),
                    author = it.author.fallbackOnEmpty("ShortX")
                )
            }
            updateState { copy(das = das, isLoading = false) }
        }.onFailure {
            logger.e(it, "loadOnlineDAs error")
            updateState { copy(isLoading = false) }
        }
    }
}