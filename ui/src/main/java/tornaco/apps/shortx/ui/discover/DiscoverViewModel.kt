package tornaco.apps.shortx.ui.discover

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.BuildProp.SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.repo.inflateOnlineFileUrl
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.mapper.randomActionColor
import tornaco.apps.shortx.ui.backup.BackupAgent
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.base.LoadState
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
    val repoIndexUrl: String

    object PrebuiltLocal : DiscoverRepo {
        override val repoIndexUrl: String
            get() = "app://shortx"
    }

    object OfficialOnline : DiscoverRepo {
        override val repoIndexUrl: String
            get() = SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL
    }

    data class CustomOnline(val indexUrl: String) : DiscoverRepo {
        override val repoIndexUrl: String
            get() = indexUrl
    }
}

data class DiscoverData(
    val rules: List<RuleUM> = emptyList(),
    val das: List<DirectActionUM> = emptyList(),
)

data class DiscoverState(
    val data: LoadState<DiscoverData> = LoadState.Loading,
    val discoverRepo: DiscoverRepo = DiscoverRepo.PrebuiltLocal,
    val customRepos: Set<DiscoveryRepoSetting> = emptySet(),
    val enableChucker: Boolean = false,
)

const val assetName = "prebuilt/prebuilt-all.zip"

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DiscoverViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<DiscoverState>(context, { DiscoverState() }) {
    private val onlineRepo = OnlineRepo(context)
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

        viewModelScope.launch {
            pref.uiEnableHttpChucker.collectLatest {
                logger.d("uiEnableHttpChucker updated: $it")
                updateState {
                    copy(
                        enableChucker = it
                    )
                }
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
        updateState {
            copy(
                discoverRepo = repo,
                data = LoadState.Loading,
            )
        }
        viewModelScope.launch {
            val url = when (repo) {
                is DiscoverRepo.CustomOnline -> {
                    repo.indexUrl
                }

                DiscoverRepo.OfficialOnline -> {
                    SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL
                }

                else -> ""
            }

            pref.selectRepo(url)
            loadDiscoverData()
        }
    }

    fun loadDiscoverData() {
        viewModelScope.launch {
            when (val repo = state.value.discoverRepo) {
                is DiscoverRepo.CustomOnline -> {
                    loadOnlineData(repo.indexUrl)
                }

                DiscoverRepo.OfficialOnline -> {
                    loadOnlineData(SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL)
                }

                DiscoverRepo.PrebuiltLocal -> {
                    loadPrebuiltData()
                }
            }
        }
    }

    private suspend fun loadPrebuiltData() {
        updateState { copy(data = LoadState.Loading) }
        kotlin.runCatching {
            val content = BackupAgent(context).readBackupPackageContent(assetName)
            val daUMs = withContext(Dispatchers.IO) {
                content.das.map { directAction ->
                    daToDirectActionUM(directAction, emptyList())
                }
            }
            val ruleUMs = withContext(Dispatchers.IO) {
                content.rules.map {
                    ruleToRuleUM(it, emptyList())
                }
            }
            updateState { copy(data = LoadState.Success(DiscoverData(ruleUMs, daUMs))) }
        }.onFailure {
            logger.e(it, "loadPrebuiltData error")
            updateState { copy(data = LoadState.Error(it)) }
        }
    }

    private suspend fun loadOnlineData(url: String) = withContext(Dispatchers.IO) {
        updateState { copy(data = LoadState.Loading) }
        kotlin.runCatching {
            val index = onlineRepo.index(url, state.value.enableChucker)
            val rules = index.rules.map {
                val installedRuleOrNull = shortXManager.getRuleById(it.id)
                RuleUM(
                    title = it.title,
                    description = it.description.fallbackOnEmpty(it.title),
                    createTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(it.updateTimeMillis)
                    ),
                    updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(it.updateTimeMillis)
                    ),
                    factIcon = it.icon ?: Remix.Development.command_line,
                    actionIcon = it.icon ?: Remix.Development.command_line,
                    factIconColor = it.iconColor?.let { it1 -> colorFromHex(it1) }?.value
                        ?: randomActionColor().value,
                    actionIconColor = it.iconColor?.let { it1 -> colorFromHex(it1) }?.value
                        ?: randomActionColor().value,
                    id = it.id,
                    runningInsCount = 0,
                    isEnabled = false,
                    rule = Rule.newBuilder().setId(it.fileUrl).build(),
                    author = it.author.fallbackOnEmpty("ShortX"),
                    isOnlineItem = true,
                    url = inflateOnlineFileUrl(url, it.fileUrl, "rule"),
                    versionCode = it.versionCode,
                    isInstalled = installedRuleOrNull != null,
                    hasUpdate = installedRuleOrNull != null && installedRuleOrNull.versionCode < it.versionCode
                )
            }
            val das = index.directActions.map {
                val installedDAOrNull = shortXManager.getDirectActionById(it.id)
                DirectActionUM(
                    title = it.title,
                    description = it.description.fallbackOnEmpty(it.title),
                    createTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(it.updateTimeMillis)
                    ),
                    updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(it.updateTimeMillis)
                    ),
                    icon = it.icon ?: Remix.Development.command_line,
                    iconColor = it.iconColor?.let { it1 -> colorFromHex(it1) }?.value
                        ?: randomActionColor().value,
                    id = it.id,
                    runningInsCount = 0,
                    directAction = DirectAction.newBuilder().setId(it.fileUrl).build(),
                    author = it.author.fallbackOnEmpty("ShortX"),
                    isOnlineItem = true,
                    url = inflateOnlineFileUrl(url, it.fileUrl, "da"),
                    versionCode = it.versionCode,
                    isInstalled = installedDAOrNull != null,
                    hasUpdate = installedDAOrNull != null && installedDAOrNull.versionCode < it.versionCode
                )
            }
            updateState { copy(data = LoadState.Success(DiscoverData(rules, das))) }
        }.onFailure {
            logger.e(it, "loadOnlineData error")
            updateState { copy(data = LoadState.Error(it)) }
        }
    }
}