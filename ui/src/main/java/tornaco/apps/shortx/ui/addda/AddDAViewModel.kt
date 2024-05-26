package tornaco.apps.shortx.ui.addda

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IActionEvaluateListener
import tornaco.apps.shortx.core.proto.action.SendSMS
import tornaco.apps.shortx.core.proto.common.ConflictStrategy
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.containsAnyTypeUrl
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.rule.Security
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.parseShareContentDA
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.maskAll
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.ShortXContextViewModelWithEffect
import tornaco.apps.shortx.ui.settings.pref.Pref
import java.net.URL
import java.util.Date
import java.util.UUID
import javax.inject.Inject

fun defaultNewDAId() = "DA" + UUID.randomUUID().toString()

data class AddDirectActionState(
    val id: String = defaultNewDAId(),
    val title: String = "",
    val description: String = "",
    val versionCode: Long = 1L,
    val isEditDirectAction: Boolean = false,
    val createTime: Long = System.currentTimeMillis(),
    val daChanged: Boolean = false,
    val copiedAction: Action? = null,
    val showAdvancedOptions: Boolean = false,
    val conflictStrategy: ConflictStrategy = ConflictStrategy.ConflictStrategy_ExecuteBoth,
    val evaluatingActions: List<String> = emptyList(),
    val daSetId: String = "",
    // For online.
    val onlineDALoadState: LoadState<Unit> = LoadState.Success(Unit),
    val securityCheckRes: Boolean = true,
    val hasSMSAction: Boolean = false,
    // Param
    val parameters: List<FuncParameter> = emptyList()
)

sealed interface AddDAEffect {
    data class DASaved(val daId: String) : AddDAEffect
    data class AddParamFailNameExists(val name: String) : AddDAEffect
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddDirectActionViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModelWithEffect<AddDirectActionState, AddDAEffect>(
        context,
        { AddDirectActionState() }) {

    val actionContentStateHandler = ActionContentStateHandler()
    val pref by lazy { Pref(context) }

    private val actionEvaluateListener = object : IActionEvaluateListener.Stub() {
        override fun onStart(id: String) {
            logger.d("actionEvaluateListener#onStart: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }

        override fun onComplete(id: String) {
            logger.d("actionEvaluateListener#onComplete: $id")
            updateState { copy(evaluatingActions = shortXManager.getEvaluatingActions()) }
        }
    }

    init {
        shortXManager.registerActionEvaluateListener(actionEvaluateListener)
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterActionEvaluateListener(actionEvaluateListener)
    }

    fun setTitle(title: String) {
        if (title != state.value.title) {
            updateState {
                copy(title = title, daChanged = true)
            }
        }
    }

    fun setDescription(description: String) {
        if (description != state.value.description) {
            updateState {
                copy(description = description, daChanged = true)
            }
        }
    }

    fun setShowAdvancedOptions(show: Boolean) {
        updateState { copy(showAdvancedOptions = show) }
    }

    fun setConflictStrategy(conflictStrategy: ConflictStrategy) {
        updateState { copy(conflictStrategy = conflictStrategy, daChanged = true) }
    }

    fun addParameter(parameter: FuncParameter) {
        if (state.value.parameters.any { it.name.lowercase() == parameter.name.lowercase() }) {
            postSideEffect(AddDAEffect.AddParamFailNameExists(parameter.name))
            return
        }
        updateState {
            copy(parameters = parameters.toMutableList().apply {
                add(parameter)
            })
        }
    }

    fun removeParameter(parameter: FuncParameter) {
        updateState {
            copy(parameters = parameters.toMutableList().apply {
                removeIf { it.name == parameter.name }
            })
        }
    }

    // Save with token to check subscribe state.
    fun save(token: String?) {
        logger.p("save with token: ${token?.maskAll()}")
        viewModelScope.launch {
            val actions = actionContentStateHandler.state.value.actions

            val protoActions = actions.map {
                it.toProtoAction().pack_()
            }
            val authorPref = pref.shareAuthorInfo.first()

            val da = DirectAction.newBuilder()
                .setId(state.value.id)
                .setCreateTime(state.value.createTime)
                .setLastUpdateTime(System.currentTimeMillis())
                .setTitle(state.value.title.fallbackOnEmpty(Date().toString()))
                .apply {
                    if (state.value.description.isNotEmpty()) {
                        description = state.value.description
                    }
                }
                .addAllActions(protoActions)
                .setAuthor(authorPref)
                .setVersionCode(state.value.versionCode)
                .setConflictPolicy(state.value.conflictStrategy)
                .setDirectActionSetId(state.value.daSetId)
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildPropInternal.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .addAllParameters(state.value.parameters)
                .build()

            shortXManager.addDirectAction(da)
            postSideEffect(AddDAEffect.DASaved(da.id))
        }
    }

    fun delete(daId: String) {
        viewModelScope.launch {
            shortXManager.deleteDirectAction(daId)
        }
    }

    fun initFromDirectActionId(daId: String?) {
        daId?.let { id ->
            initFromDirectAction(shortXManager.getDirectActionById(id))
        }
    }

    fun initDASetIdIfNonNull(daSetId: String?) {
        daSetId?.let {
            updateState { copy(daSetId = daSetId) }
        }
    }

    fun initFromDirectAction(action: DirectAction?) {
        viewModelScope.launch {
            action?.let { da ->
                logger.d("initFromDirectAction: $da")

                actionContentStateHandler.init(da.actionsList.mapNotNull {
                    it.toAction(
                        shortXManager
                    )
                })
                val smsTyprUrls = listOf(
                    SendSMS.getDefaultInstance().pack_().typeUrl,
                )
                val hasAnySMSAction = da.containsAnyTypeUrl(smsTyprUrls)
                val hasAnyParam = da.parametersList.isNotEmpty()
                updateState {
                    copy(
                        id = da.id,
                        title = da.title,
                        description = da.description,
                        isEditDirectAction = true,
                        createTime = da.createTime,
                        versionCode = da.versionCode,
                        conflictStrategy = da.conflictPolicy,
                        evaluatingActions = shortXManager.getEvaluatingActions(),
                        daSetId = da.directActionSetId,
                        securityCheckRes = Security.checkSecurity(da),
                        hasSMSAction = hasAnySMSAction,
                        parameters = da.parametersList,
                        showAdvancedOptions = hasAnyParam
                    )
                }
            }
        }
    }

    fun initFromUrl(url: String?) {
        url?.let { _ ->
            logger.d("initFromUrl: $url")
            viewModelScope.launch {
                updateState { copy(onlineDALoadState = LoadState.Loading) }
                withContext(Dispatchers.IO) {
                    if (BuildPropInternal.APP_IS_DEBUG) {
                        delay(1000)
                    }
                    runCatching {
                        val content = URL(url).readText()
                        val da = parseShareContentDA(content)
                        updateState {
                            copy(onlineDALoadState = LoadState.Success(Unit))
                        }
                        requireNotNull(da) { "Fail to parse Direct action: \n$content" }
                        initFromDirectAction(da)
                    }.onFailure {
                        logger.e(it)
                        updateState {
                            copy(onlineDALoadState = LoadState.Error(it))
                        }
                    }
                }
            }
        }
    }
}