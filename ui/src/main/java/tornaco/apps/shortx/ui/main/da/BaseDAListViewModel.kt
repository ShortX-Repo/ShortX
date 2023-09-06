package tornaco.apps.shortx.ui.main.da

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.BuildProp
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
import tornaco.apps.shortx.ui.addda.defaultNewDAId
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.main.DASortBy
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.settings.ResetEventRegistry
import tornaco.apps.shortx.ui.settings.RestoreEventRegistry
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.share.addShareContentHeader
import java.util.*

data class DAListState(
    val das: List<DirectActionUM> = emptyList(),
    val daSortBy: DASortBy = DASortBy.CreateTime,
    val isShowDetailedDAItem: Boolean = true,
    val isLoading: Boolean = true,
)

@SuppressLint("StaticFieldLeak")
abstract class BaseDAListViewModel(context: Context) :
    ContextViewModel<DAListState>(context, { DAListState() }) {
    private val pref by lazy { Pref(context) }
    private var loadDAJob: Job? = null

    private val jobStatusListener = object : IJobStatusListener.Stub() {
        override fun onJobStarted(id: String?) {
            logger.d("onJobStarted: $id")
            delayLoadDAList()
        }

        override fun onJobCompleted(id: String?) {
            logger.d("onJobCompleted: $id")
            delayLoadDAList()
        }

        override fun onJobCancelled(id: String?) {
            logger.d("onJobCancelled: $id")
            delayLoadDAList()
        }
    }

    private val daObs = object : DAObserver() {
        override fun onDAChanged(id: String) {
            super.onDAChanged(id)
            logger.d("daObs, onDAChanged: $id")
            loadDAList()
        }

        override fun onDASetChanged(id: String) {
            super.onDASetChanged(id)
            logger.d("daObs, onDASetChanged: $id")
            loadDAList()
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
                loadDAList()
            }
        }

        viewModelScope.launch {
            RestoreEventRegistry.dataRestoreComplete.collect {
                logger.d("reload data since data restored")
                loadDAList()
            }
        }
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


    abstract fun loadDAList(delay: Boolean = false)

    private fun delayLoadDAList(delay: Long = 200L) {
        loadDAJob?.cancel()
        loadDAJob = viewModelScope.launch {
            delay(delay)
            loadDAList()
        }
    }

    fun deleteDA(daId: String) {
        viewModelScope.launch {
            shortXManager.deleteDirectAction(daId)
        }
    }

    fun dupDA(daId: String, token: String?) {
        viewModelScope.launch {
            shortXManager.getDirectActionById(daId)?.let {
                importDA(it, true, token)
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

    fun stopJobs(da: DirectActionUM) {
        viewModelScope.launch {
            shortXManager.cancelJobs(listOf(da.id))
        }
    }


    fun daToShareString(da: DirectActionUM): String? =
        shortXManager.getDirectActionById(da.id)?.toJson()
            ?.addShareContentHeader(mapOf("type" to "da"))

    fun importDA(da: DirectAction, shouldChangeId: Boolean, token: String?) {
        val daWithSameNameExists =
            state.value.das.firstOrNull { it.title == da.title } != null
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
                            add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
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
                .setTitle(title).setId("DS-${UUID.randomUUID()}")
                .setCreateTime(System.currentTimeMillis())
                .setLastUpdateTime(System.currentTimeMillis())
                .build()
        )
    }

    fun moveToSet(da: DirectActionUM, setId: String, token: String?) {
        shortXManager.addDirectAction(
            DirectAction.newBuilder(da.directAction).setDirectActionSetId(setId)
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()
        )
    }

    fun moveOutSet(da: DirectAction, token: String?) {
        shortXManager.addDirectAction(
            DirectAction.newBuilder(da).setDirectActionSetId("")
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()
        )
    }
}