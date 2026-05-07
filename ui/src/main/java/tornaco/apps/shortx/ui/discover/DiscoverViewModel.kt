package tornaco.apps.shortx.ui.discover

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.github.promeg.pinyinhelper.Pinyin
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter
import tornaco.apps.shortx.core.BuildProp.SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL
import tornaco.apps.shortx.core.BuildProp.SHORTX_DISCOVERY_GITHUB_TELEGRAM_ONLINE_REPO_URL
import tornaco.apps.shortx.core.proto.common.CodeLibraryItem
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.proto.shortXProtoVersion
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.repo.inflateOnlineFileUrl
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.mapper.randomActionColor
import tornaco.apps.shortx.ui.backup.BackupAgent
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.base.colorFromHex
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.model.daToDirectActionUM
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import tornaco.apps.shortx.ui.remote.OnlineRepo
import tornaco.apps.shortx.ui.settings.pref.DiscoveryRepoSetting
import tornaco.apps.shortx.ui.settings.pref.Pref
import java.util.Date
import javax.inject.Inject

sealed interface DiscoverRepo {
    val repoIndexUrl: String

    data object PrebuiltLocal : DiscoverRepo {
        override val repoIndexUrl: String
            get() = "app://shortx"
    }

    data object OfficialOnline : DiscoverRepo {
        override val repoIndexUrl: String
            get() = SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL
    }

    data object TelegramOnline : DiscoverRepo {
        override val repoIndexUrl: String
            get() = SHORTX_DISCOVERY_GITHUB_TELEGRAM_ONLINE_REPO_URL
    }

    data class CustomOnline(val indexUrl: String) : DiscoverRepo {
        override val repoIndexUrl: String
            get() = indexUrl
    }
}

data class DiscoverData(
    val rules: List<RuleUM> = emptyList(),
    val das: List<DirectActionUM> = emptyList(),
    val codeLibraries: List<DiscoverCodeLibraryUM> = emptyList(),
)

data class DiscoverCodeLibraryUM(
    val id: String,
    val title: String,
    val titlePinyin: String,
    val description: String,
    val updateTime: String,
    val createTime: String,
    val type: CodeType = CodeType.UNRECOGNIZED,
    val icon: String = Remix.Development.code_line,
    val iconColor: ULong,
    val versionCode: Long = 0L,
    val author: String = "ShortX",
    val isOnlineItem: Boolean = false,
    val url: String? = null,
    val item: CodeLibraryItem? = null,
    val isInstalled: Boolean = false,
    val hasUpdate: Boolean = false,
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
    ShortXContextViewModel<DiscoverState>(context, { DiscoverState() }) {
    private val onlineRepo = OnlineRepo(context)
    private val pref by lazy { Pref(context) }
    private val currentProtoVersion = shortXProtoVersion
    private var currentJob: Job? = null

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

                    SHORTX_DISCOVERY_GITHUB_TELEGRAM_ONLINE_REPO_URL -> {
                        DiscoverRepo.TelegramOnline
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
        logger.d("switchRepo: $repo")
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

                DiscoverRepo.TelegramOnline -> {
                    SHORTX_DISCOVERY_GITHUB_TELEGRAM_ONLINE_REPO_URL
                }

                else -> ""
            }

            pref.selectRepo(url)
            loadDiscoverData()
        }
    }

    fun loadDiscoverData() {
        logger.d("loadDiscoverData.")
        currentJob?.cancel()
        val job = viewModelScope.launch {
            when (val repo = state.value.discoverRepo) {
                is DiscoverRepo.CustomOnline -> {
                    loadOnlineData(repo.indexUrl)
                }

                DiscoverRepo.OfficialOnline -> {
                    loadOnlineData(SHORTX_DISCOVERY_DEFAULT_ONLINE_REPO_URL)
                }

                DiscoverRepo.TelegramOnline -> {
                    loadOnlineData(SHORTX_DISCOVERY_GITHUB_TELEGRAM_ONLINE_REPO_URL)
                }

                DiscoverRepo.PrebuiltLocal -> {
                    loadPrebuiltData()
                }
            }
        }
        currentJob = job
    }

    private suspend fun loadPrebuiltData() {
        logger.d("loadPrebuiltData.")
        updateState { copy(data = LoadState.Loading) }
        kotlin.runCatching {
            val content = BackupAgent(context).readBackupPackageContent(assetName)
            val daUMs = withContext(Dispatchers.IO) {
                content.das.mapNotNull { directAction ->
                    runCatching {
                        daToDirectActionUM(directAction, emptyList())
                    }.getOrElse {
                        logger.e(it, "daToDirectActionUM")
                        null
                    }
                }
            }
            val ruleUMs = withContext(Dispatchers.IO) {
                content.rules.mapNotNull {
                    runCatching {
                        // Disable it, in-case it is enabled after import.
                        ruleToRuleUM(it, emptyList()).copy(isEnabled = false)
                    }.getOrElse {
                        logger.e(it, "ruleToRuleUM")
                        null
                    }
                }
            }
            val codeLibraryUMs = withContext(Dispatchers.IO) {
                content.codeLibraries.mapNotNull { item ->
                    runCatching {
                        val installedItem = shortXManager.getCodeLibraryItemById(item.id)
                        DiscoverCodeLibraryUM(
                            id = item.id,
                            title = item.name,
                            titlePinyin = Pinyin.toPinyin(item.name, "").lowercase(),
                            description = item.description.fallbackOnEmpty(item.name),
                            createTime = FuzzyDateTimeFormatter.getTimeAgo(
                                context,
                                Date(item.createdAt)
                            ),
                            updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                                context,
                                Date(item.updatedAt)
                            ),
                            type = item.type,
                            icon = codeIconName(item.type),
                            iconColor = codeIconColor(item.type),
                            author = "ShortX",
                            item = item,
                            isInstalled = installedItem != null,
                            hasUpdate = installedItem != null && installedItem.updatedAt < item.updatedAt,
                        )
                    }.getOrElse {
                        logger.e(it, "codeLibraryToDiscoverUM")
                        null
                    }
                }
            }
            logger.d("loadPrebuiltData done.")
            updateState {
                copy(
                    data = LoadState.Success(
                        DiscoverData(
                            rules = ruleUMs,
                            das = daUMs,
                            codeLibraries = codeLibraryUMs,
                        )
                    )
                )
            }
        }.onFailure {
            logger.e(it, "loadPrebuiltData error")
            if (it !is CancellationException) updateState { copy(data = LoadState.Error(it)) }
        }
    }

    private suspend fun loadOnlineData(url: String) = withContext(Dispatchers.IO) {
        logger.d("loadOnlineData $url")
        updateState { copy(data = LoadState.Loading) }
        kotlin.runCatching {
            val index = onlineRepo.index(url, state.value.enableChucker)
            val rules = index.rules.filter { r ->
                val requireVersion = r.requireMinShortXProtoVersion
                (requireVersion == null || currentProtoVersion >= requireVersion).also {
                    if (!it) {
                        logger.w("Rule ${r.title} is not compatible with current ShortX version, require version: $requireVersion, current version: $currentProtoVersion")
                    }
                }
            }.sortedByDescending { it.updateTimeMillis }.map {
                val installedRuleOrNull = shortXManager.getRuleById(it.id)
                RuleUM(
                    title = it.title,
                    titlePinyin = Pinyin.toPinyin(it.title, "").lowercase(),
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
                    isEnabled = false,
                    rule = Rule.newBuilder().setId(it.fileUrl).build(),
                    author = it.author.fallbackOnEmpty("ShortX"),
                    isOnlineItem = true,
                    url = inflateOnlineFileUrl(url, it.fileUrl, "rule"),
                    versionCode = it.versionCode,
                    isInstalled = installedRuleOrNull != null,
                    hasUpdate = installedRuleOrNull != null && installedRuleOrNull.versionCode < it.versionCode,
                    hasAnyParameter = false
                )
            }
            val das = index.directActions.filter { da ->
                val requireVersion = da.requireMinShortXProtoVersion
                logger.d("requireVersion: $requireVersion")
                (requireVersion == null || currentProtoVersion >= requireVersion).also {
                    if (!it) {
                        logger.w("DirectAction ${da.title} is not compatible with current ShortX version, require version: $requireVersion, current version: $currentProtoVersion")
                    }
                }
            }.sortedByDescending { it.updateTimeMillis }.map {
                val installedDAOrNull = shortXManager.getDirectActionById(it.id)
                DirectActionUM(
                    title = it.title,
                    titlePinyin = Pinyin.toPinyin(it.title, "").lowercase(),
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
                    directAction = DirectAction.newBuilder().setId(it.fileUrl).build(),
                    author = it.author.fallbackOnEmpty("ShortX"),
                    isOnlineItem = true,
                    url = inflateOnlineFileUrl(url, it.fileUrl, "da"),
                    versionCode = it.versionCode,
                    isInstalled = installedDAOrNull != null,
                    hasUpdate = installedDAOrNull != null && installedDAOrNull.versionCode < it.versionCode,
                    hasAnyParameter = false
                )
            }
            val codeLibraries = index.codeLibraries.orEmpty().filter { code ->
                val requireVersion = code.requireMinShortXProtoVersion
                (requireVersion == null || currentProtoVersion >= requireVersion).also {
                    if (!it) {
                        logger.w("CodeLibrary ${code.title} is not compatible with current ShortX version, require version: $requireVersion, current version: $currentProtoVersion")
                    }
                }
            }.sortedByDescending { it.updateTimeMillis }.map {
                val installedCodeOrNull = shortXManager.getCodeLibraryItemById(it.id)
                DiscoverCodeLibraryUM(
                    id = it.id,
                    title = it.title,
                    titlePinyin = Pinyin.toPinyin(it.title, "").lowercase(),
                    description = it.description.fallbackOnEmpty(it.title),
                    createTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(it.updateTimeMillis)
                    ),
                    updateTime = FuzzyDateTimeFormatter.getTimeAgo(
                        context,
                        Date(it.updateTimeMillis)
                    ),
                    type = CodeType.UNRECOGNIZED,
                    icon = it.icon ?: Remix.Development.code_line,
                    iconColor = it.iconColor?.let { iconColor -> colorFromHex(iconColor) }?.value
                        ?: codeIconColor(CodeType.UNRECOGNIZED),
                    versionCode = it.versionCode,
                    author = it.author.fallbackOnEmpty("ShortX"),
                    isOnlineItem = true,
                    url = inflateOnlineFileUrl(url, it.fileUrl, "code"),
                    isInstalled = installedCodeOrNull != null,
                    hasUpdate = installedCodeOrNull != null && installedCodeOrNull.updatedAt < it.updateTimeMillis,
                )
            }
            logger.d("loadOnlineData done $url")
            updateState {
                copy(
                    data = LoadState.Success(
                        DiscoverData(
                            rules = rules,
                            das = das,
                            codeLibraries = codeLibraries,
                        )
                    )
                )
            }
        }.onFailure {
            logger.e(it, "loadOnlineData error")
            if (it !is CancellationException) updateState { copy(data = LoadState.Error(it)) }
        }
    }
}

private fun codeIconName(type: CodeType): String {
    return when (type) {
        CodeType.CodeType_MVEL -> Remix.Development.code_line
        CodeType.CodeType_JAVASCRIPT -> Remix.Logos.vuejs_line
        else -> Remix.Development.code_line
    }
}

private fun codeIconColor(type: CodeType): ULong {
    return when (type) {
        CodeType.CodeType_MVEL -> colorFromHex("#6750A4").value
        CodeType.CodeType_JAVASCRIPT -> colorFromHex("#FF9800").value
        else -> colorFromHex("#607D8B").value
    }
}