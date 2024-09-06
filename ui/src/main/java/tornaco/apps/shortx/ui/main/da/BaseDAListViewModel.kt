package tornaco.apps.shortx.ui.main.da

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.github.promeg.pinyinhelper.Pinyin
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IJobStatusListener
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.da.DirectActionSet
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.toJson
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.rule.DAObserver
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.removeSpecialCharacters
import tornaco.apps.shortx.ui.addda.defaultNewDAId
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.LoadState.Loading.data
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.main.CommonSetFilter
import tornaco.apps.shortx.ui.main.DASortBy
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.settings.ResetEventRegistry
import tornaco.apps.shortx.ui.settings.RestoreEventRegistry
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.share.SharedData
import tornaco.apps.shortx.ui.share.addShareContentHeader
import tornaco.apps.shortx.ui.shortcut.ApkInstallState
import tornaco.apps.shortx.ui.shortcut.ShortcutStubInfo
import tornaco.apps.shortx.ui.shortcut.ShortcutStubInstaller
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import java.util.UUID

fun defaultNewDASetId() = "DS-${UUID.randomUUID()}"

data class DAListState(
    val daSortBy: DASortBy = DASortBy.CreateTime,
    val isShowDetailedDAItem: Boolean = true,
    val colorfulCard: Boolean = false,
    val searchQuery: String = ""
)

data class DAList(
    val das: List<DirectActionUM> = emptyList(),
    val daSetFilters: List<CommonSetFilter> = emptyList(),
    val selectedDASetFilter: CommonSetFilter = CommonSetFilter.PinnedAll
)

data class ActiveJobIds(
    val ids: List<String> = emptyList(),
)

@SuppressLint("StaticFieldLeak")
abstract class BaseDAListViewModel(context: Context) :
    ShortXContextViewModel<DAListState>(context, { DAListState() }) {
    protected val pref by lazy { Pref(context) }

    private val _shortcutApkInstalled = MutableSharedFlow<ApkInstallState>()
    val shortcutApkInstalled = _shortcutApkInstalled.asSharedFlow()

    private val loadActiveJobRequest: MutableStateFlow<Long> =
        MutableStateFlow(System.currentTimeMillis())
    protected val loadDARequest: MutableStateFlow<Long> =
        MutableStateFlow(System.currentTimeMillis())

    abstract val daList: StateFlow<LoadState<DAList>>

    @OptIn(FlowPreview::class)
    val activeJobIds: StateFlow<ActiveJobIds> by lazy {
        loadActiveJobRequest.sample(300).map {
            ActiveJobIds(shortXManager.getActiveJobs().apply {
                logger.d("activeJobs: $this")
            })
        }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = ActiveJobIds())
    }

    private val jobStatusListener = object : IJobStatusListener.Stub() {
        override fun onJobStarted(id: String?) {
            logger.d("onJobStarted: $id")
            requestLoadActiveJobs()
        }

        override fun onJobCompleted(id: String?) {
            logger.d("onJobCompleted: $id")
            requestLoadActiveJobs()
        }

        override fun onJobCancelled(id: String?) {
            logger.d("onJobCancelled: $id")
            requestLoadActiveJobs()
        }
    }

    private val daObs = object : DAObserver() {
        override fun onDAChanged(id: String) {
            super.onDAChanged(id)
            logger.d("daObs, onDAChanged: $id")
            requestLoadDAList()

            if (id.isEmpty()) {
                LVLStateHolder.fab()
            }
        }

        override fun onDASetChanged(id: String) {
            super.onDASetChanged(id)
            logger.d("daObs, onDASetChanged: $id")
            requestLoadDAList()
        }
    }

    init {
        shortXManager.registerJobStatusListener(jobStatusListener)
        shortXManager.registerDAObs(daObs)

        viewModelScope.launch {
            pref.uiShowDetailedDAItemContent.collectLatest {
                logger.d("uiShowDetailedDAItemContent changed: $it")
                updateState { copy(isShowDetailedDAItem = it) }
            }
        }

        viewModelScope.launch {
            ResetEventRegistry.dataResetComplete.collect {
                logger.d("reload data since data has been reset")
                requestLoadDAList()
            }
        }

        viewModelScope.launch {
            RestoreEventRegistry.dataRestoreComplete.collect {
                logger.d("reload data since data restored")
                requestLoadDAList()
            }
        }

        viewModelScope.launch {
            pref.uiColorfulCard.collectLatest {
                updateState { copy(colorfulCard = it) }
            }
        }

        requestLoadActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterJobStatusListener(jobStatusListener)
        shortXManager.unregisterDAObs(daObs)
    }

    fun toggleShowDetailedDAItem() {
        viewModelScope.launch {
            pref.setShowDetailedDAItemContent(!state.value.isShowDetailedDAItem)
        }
    }

    fun requestLoadDAList() {
        loadDARequest.update { System.currentTimeMillis() }
    }

    fun requestLoadActiveJobs() {
        loadActiveJobRequest.update { System.currentTimeMillis() }
    }

    fun deleteDA(daId: String) {
        viewModelScope.launch {
            shortXManager.deleteDirectAction(daId)
        }
    }

    fun dupDA(daId: String, token: String?) {
        viewModelScope.launch {
            shortXManager.getDirectActionById(daId)?.let {
                importDA(da = it, shouldChangeId = true, token = token)
            }
        }
    }

    fun executeDA(da: DirectActionUM) {
        viewModelScope.launch {
            val evaluateContext = EvaluateContext.newBuilder()
                .setSrcId(da.id)
                .setSrcEvent(ContextSrcEvent.ContextSrc_DirectAction)
                .setSrcTitle(da.title)
                .build()
            shortXManager.executeDirectionActionById(
                context = evaluateContext,
                id = da.id
            )
        }
    }

    fun createApk(activity: Context, da: DirectActionUM) {
        viewModelScope.launch {
            val pkgNameSuffix = Pinyin.toPinyin(da.title, "").lowercase().removeSpecialCharacters()
            val info = ShortcutStubInfo(
                appLabel = da.title,
                appPkgName = "shortx.stub.${pkgNameSuffix}",
                appIcon = da.icon,
                appIconTintColor = Color.WHITE,
                appIconBgColor = androidx.compose.ui.graphics.Color(da.iconColor).toArgb(),
                daId = da.id
            )
            ShortcutStubInstaller.doInstallShortcutApk(
                activity,
                info,
                effect = {
                    viewModelScope.launch {
                        _shortcutApkInstalled.emit(it)
                    }
                }
            )
        }
    }

    fun stopJobs(da: DirectActionUM) {
        viewModelScope.launch {
            shortXManager.cancelJobs(listOf(da.id))
        }
    }


    fun daToShareString(da: DirectActionUM): String? =
        shortXManager.getDirectActionById(da.id)?.let {
            DirectAction.newBuilder(it)
                .setId("SHARED-" + defaultNewDAId())
                .build()
        }?.toJson()
            ?.addShareContentHeader(mapOf("type" to SharedData.TYPE_DA))

    fun daSetToShareString(da: DirectActionUM): String? =
        shortXManager.getDASetById(da.id, true)?.let {
            DirectActionSet.newBuilder(it)
                .setId("SHARE-" + defaultNewDASetId())
                .build()
        }?.toJson()
            ?.addShareContentHeader(mapOf("type" to SharedData.TYPE_DA_SET))

    fun importDA(da: DirectAction, shouldChangeId: Boolean, token: String?) {
        val daWithSameNameExists =
            daList.value.data?.das?.firstOrNull { it.title == da.title } != null
        val copyInd = " (Copy)"

        viewModelScope.launch {
            val refinedActions = da.actionsList.mapNotNull {
                it.toAction(shortXManager)?.toProtoAction(defaultNewActionId())?.pack_()
            }

            val refined =
                DirectAction.newBuilder(da)
                    .setTitle(if (daWithSameNameExists) "${da.title} $copyInd" else da.title)
                    .apply {
                        if (shouldChangeId) id = defaultNewDAId()
                    }
                    .clearActions()
                    .addAllActions(refinedActions)
                    .setCreateTime(da.createTime)
                    .setLastUpdateTime(da.lastUpdateTime)
                    .setVersionCode(da.versionCode)
                    .addAllExtras(mutableListOf<StringPair>().apply {
                        if (!token.isNullOrEmpty()) {
                            add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                        }
                    })
                    .build()
            shortXManager.addDirectAction(refined)
        }
    }

    fun createNewDASet(title: String) {
        shortXManager.addOrUpdateDASet(
            DirectActionSet
                .newBuilder()
                .setTitle(title).setId(defaultNewDASetId())
                .setCreateTime(System.currentTimeMillis())
                .setLastUpdateTime(System.currentTimeMillis())
                .build()
        )
    }

    fun moveToSet(da: DirectActionUM, setId: String, token: String?) {
        // Should load new da here, because it maybe changed after DA list is loaded.
        val originalDA = shortXManager.getDirectActionById(da.id) ?: return
        shortXManager.addDirectAction(
            DirectAction.newBuilder(originalDA).setDirectActionSetId(setId)
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()
        )
    }

    fun moveOutSet(da: DirectAction, token: String?) {
        // Should load new da here, because it maybe changed after DA list is loaded.
        val originalDA = shortXManager.getDirectActionById(da.id) ?: return
        shortXManager.addDirectAction(
            DirectAction.newBuilder(originalDA).setDirectActionSetId("")
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()
        )
    }

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            updateState { copy(searchQuery = query) }
        }
    }
}