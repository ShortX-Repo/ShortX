package tornaco.apps.shortx.ui.addtoggle

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.proto.toggles.ToggleStateHandleByCondition
import tornaco.apps.shortx.core.proto.toggles.ToggleStateHandleByStore
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.maskAll
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.addrule.condition.ConditionContentStateHandler
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.condition.op.toConditionOperator
import tornaco.apps.shortx.ui.addrule.condition.op.toOp
import tornaco.apps.shortx.ui.addtoggle.model.ToggleStateHandle
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.settings.pref.Pref
import java.util.*
import javax.inject.Inject

fun defaultNewToggleId() = "toggle-" + UUID.randomUUID().toString()

data class AddToggleState(
    val id: String = defaultNewToggleId(),
    val title: String = "",
    val description: String = "",
    val versionCode: Long = 1L,
    val isEditToggle: Boolean = false,
    val createTime: Long = System.currentTimeMillis(),
    val toggleChanged: Boolean = false,
    val isEnabled: Boolean = false,
    val stateHandle: ToggleStateHandle = ToggleStateHandle.ByStore,
    val evaluatingActions: List<String> = emptyList(),
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddToggleViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AddToggleState>(context, { AddToggleState() }) {

    private val _toggleSaved = MutableSharedFlow<String>()
    val toggleSaved = _toggleSaved.asSharedFlow()

    val pref by lazy { Pref(context) }

    val actionOnEnabledStateHandler = ActionContentStateHandler()
    val actionOnDisabledStateHandler = ActionContentStateHandler()
    val actionOnDeletedStateHandler = ActionContentStateHandler()

    val conditionContentStateHandler = ConditionContentStateHandler()

    fun setTitle(title: String) {
        if (title != state.value.title) {
            updateState {
                copy(title = title, toggleChanged = true)
            }
        }
    }

    fun setDescription(description: String) {
        if (description != state.value.description) {
            updateState {
                copy(description = description, toggleChanged = true)
            }
        }
    }

    fun setStateHandle(handle: ToggleStateHandle) {
        updateState {
            copy(stateHandle = handle, toggleChanged = true)
        }
    }

    // Save with token to check subscribe state.
    fun save(token: String?) {
        logger.p("save with token: ${token?.maskAll()}")
        viewModelScope.launch {
            val authorPref = pref.shareAuthorInfo.first()

            val actionsOnEnabled = actionOnEnabledStateHandler.actions
            val actionsOnDisabled = actionOnDisabledStateHandler.actions
            val actionsOnDeleted = actionOnDeletedStateHandler.actions

            val conditions = conditionContentStateHandler.conditions
            val condOp = conditionContentStateHandler.conditionOp

            val stateHandle: ProtoAny = when (state.value.stateHandle) {
                is ToggleStateHandle.ByCondition -> {
                    ToggleStateHandleByCondition.newBuilder()
                        .addAllConditions(conditions.map {
                            it.toProtoCondition().pack_()
                        })
                        .setCondOp(condOp.toConditionOperator())
                        .apply {
                            if (condOp is Op.MVEL) {
                                condOpPayload =
                                    ConditionOperatorPayload
                                        .newBuilder()
                                        .setExpression(condOp.expression)
                                        .build()
                            }
                        }
                        .build().pack_()
                }

                ToggleStateHandle.ByStore -> {
                    ToggleStateHandleByStore.getDefaultInstance().pack_()
                }
            }

            val toggle = Toggle.newBuilder()
                .setId(state.value.id)
                .setCreateTime(state.value.createTime)
                .setLastUpdateTime(System.currentTimeMillis())
                .setTitle(
                    state.value.title.fallbackOnEmpty(
                        DateUtils.formatHHMMSS(
                            System.currentTimeMillis()
                        )
                    )
                )
                .setDescription(state.value.description.fallbackOnEmpty(""))
                .setIsEnabled(state.value.isEnabled)
                .setAuthor(authorPref)
                .setVersionCode(state.value.versionCode)
                .addAllActionsOnEnabled(actionsOnEnabled.map {
                    it.toProtoAction().pack_()
                })
                .addAllActionsOnDisabled(actionsOnDisabled.map {
                    it.toProtoAction().pack_()
                })
                .addAllActionsOnDeleted(actionsOnDeleted.map {
                    it.toProtoAction().pack_()
                })
                .setStateHandle(stateHandle)
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()

            shortXManager.addToggle(toggle)
            _toggleSaved.emit(toggle.id)
        }
    }

    fun delete(toggleId: String) {
        viewModelScope.launch {
            shortXManager.deleteToggle(toggleId)
        }
    }

    fun initFromToggleId(toggleId: String?) {
        toggleId?.let { id ->
            viewModelScope.launch {
                shortXManager.getToggleById(id)?.let { toggle ->
                    logger.d("initFromToggle: $toggle")
                    initFromToggle(toggle)
                }
            }
        }
    }

    fun initFromToggle(toggle: Toggle?) {
        viewModelScope.launch {
            toggle?.let { tg ->
                logger.d("initFromToggle: $tg")
                updateState {
                    copy(
                        id = tg.id,
                        title = tg.title,
                        description = tg.description,
                        versionCode = tg.versionCode,
                        isEditToggle = true,
                        createTime = tg.createTime,
                        isEnabled = tg.isEnabled,
                    )
                }

                toggle.stateHandle.let { protoAny ->
                    if (protoAny is_ ToggleStateHandleByStore::class.java) {
                        updateState {
                            copy(stateHandle = ToggleStateHandle.ByStore)
                        }
                    } else if (protoAny is_ ToggleStateHandleByCondition::class.java) {
                        val byCond = protoAny unpack_ ToggleStateHandleByCondition::class.java

                        conditionContentStateHandler.init(
                            conditions = byCond.conditionsList.mapNotNull {
                                it.toCondition(
                                    shortXManager
                                )
                            },
                            conditionOp = byCond.condOp.toOp(byCond.condOpPayload)
                        )

                        updateState {
                            copy(stateHandle = ToggleStateHandle.ByCondition)
                        }
                    }
                }

                actionOnEnabledStateHandler.init(
                    actions = tg.actionsOnEnabledList.mapNotNull {
                        it.toAction(
                            shortXManager
                        )
                    })

                actionOnDisabledStateHandler.init(
                    actions = tg.actionsOnDisabledList.mapNotNull {
                        it.toAction(
                            shortXManager
                        )
                    })

                actionOnDeletedStateHandler.init(
                    actions = tg.actionsOnDeletedList.mapNotNull {
                        it.toAction(
                            shortXManager
                        )
                    })
            }
        }
    }
}