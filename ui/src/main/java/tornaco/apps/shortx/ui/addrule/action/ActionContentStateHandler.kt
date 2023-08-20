package tornaco.apps.shortx.ui.addrule.action

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.UiClipboard
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.WithState
import tornaco.apps.shortx.ui.base.WithStateImpl
import tornaco.apps.shortx.ui.base.i18NIns
import java.util.Date

data class ActionContentState(
    val actions: List<Action> = emptyList(),
    val actionWarningMessages: Map<String, List<String>> = emptyMap(),
    val isChanged: Boolean = false,
)

fun ActionContentStateHandler.asActionHandler(): ActionHandler {
    return ActionHandler(
        addAction = { action ->
            this.addAction(
                action
            )
        },
        insertAbove = { action, anchorId ->
            this.insertActionAbove(action, anchorId)
        },
        insertBelow = { action, anchorId ->
            this.insertActionBelow(action, anchorId)
        },
        copy = { actionId ->
            actions.first { it.id == actionId }.let {
                UiClipboard.copy(UiClipboard.ClipboardContent.ActionContent(it))
            }
        },
        enableDisable = { actionId ->
            actions.first { it.id == actionId }.let {
                addAction(
                    it.clone(
                        id = it.id,
                        note = it.note,
                        actionOnError = it.actionOnError,
                        isEnabled = !it.isEnabled,
                        contextData = it.contextData
                    )
                )
            }

        },
        setOnError = { actionId, onError ->
            actions.first { it.id == actionId }.let { action ->
                addAction(
                    action.clone(
                        id = action.id,
                        note = action.note,
                        isEnabled = action.isEnabled,
                        actionOnError = onError,
                        contextData = action.contextData
                    )
                )
            }
        },
        removeAction = { action ->
            this.removeAction(action)
        },
        applySort = { sorted ->
            this.sortActions(sorted)
        },
        setNote = { actionId, note ->
            actions.first { it.id == actionId }.let { action ->
                this.setNote(action, note)
            }
        },
        execute = { actionId ->
            actions.first { it.id == actionId }.let { this.executeAction(it) }
        },
        executeAll = {
            this.executeAllAction()
        },
        setCustomContextDataKey = { actionId, key ->
            actions.first { it.id == actionId }.let { action ->
                addAction(
                    action.clone(
                        id = action.id,
                        note = action.note,
                        isEnabled = action.isEnabled,
                        actionOnError = action.actionOnError,
                        contextData = action.contextData.toMutableList().apply {
                            val targetData = first { it.key == key.first }
                            val index = indexOf(targetData)
                            set(index, targetData.copy(customKey = key.second))
                        }
                    )
                )
            }
        }
    )
}

class ActionContentStateHandler(
    private val withState: WithStateImpl<ActionContentState> = WithStateImpl { ActionContentState() }
) : WithState<ActionContentState> by withState {
    private val shortXManager = tornaco.apps.shortx.core.shortXManager
    private val logger = Logger("ActionContentStateHandler")
    private val i18N = i18NIns
    private val errorChecker = ActionErrorChecker()

    val actions get() = state.value.actions

    fun addAction(action: Action) {
        logger.d("addAction: ${action.id}")
        updateState {
            copy(actions = actions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == action.id }
                logger.d("addAction, existing index: $existingIndex")
                if (existingIndex >= 0) {
                    set(existingIndex, action)
                } else {
                    add(action)
                }
            }, isChanged = true)
        }
        checkUpdateWarnings()
    }

    fun insertActionAbove(action: Action, anchorId: String) {
        val refinedAction = requireNotNull(
            action.toProtoAction(overrideId = defaultNewActionId()).pack_()
                .toAction(shortXManager)
        ) {
            "refinedAction is null"
        }
        updateState {
            copy(actions = actions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == anchorId }
                if (existingIndex >= 0) {
                    add(existingIndex, refinedAction)
                } else {
                    add(refinedAction)
                }
            }, isChanged = true)
        }
        checkUpdateWarnings()
    }

    fun insertActionBelow(action: Action, anchorId: String) {
        val refinedAction = requireNotNull(
            action.toProtoAction(overrideId = defaultNewActionId()).pack_()
                .toAction(shortXManager)
        ) {
            "refinedAction is null"
        }
        updateState {
            copy(actions = actions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == anchorId }
                if (existingIndex >= 0) {
                    add(existingIndex + 1, refinedAction)
                } else {
                    add(refinedAction)
                }
            }, isChanged = true)
        }
        checkUpdateWarnings()
    }

    fun removeAction(action: Action) {
        updateState {
            copy(actions = actions.toMutableList().apply {
                remove(action)
            }, isChanged = true)
        }
        checkUpdateWarnings()
    }

    fun sortActions(actions: List<Action>) {
        updateState {
            copy(actions = actions, isChanged = true)
        }
    }

    fun setNote(action: Action, note: String) {
        action.toProtoAction(overrideNote = note)
            .pack_().toAction(shortXManager)?.let { addAction(it) }
    }

    fun init(actions: List<Action>) {
        updateState { copy(actions = actions) }
        checkUpdateWarnings()
    }

    private fun checkUpdateWarnings() {
        val map: Map<String, List<String>> = state.value.actions.groupBy {
            it.id
        }.mapValues {
            errorChecker.generateWarningMessages(
                it.value,
                state.value.actions,
                shortXManager,
                i18N
            )
        }
        updateState {
            copy(
                actionWarningMessages = map
            )
        }
    }

    suspend fun executeAction(action: Action) {
        withContext(Dispatchers.IO) {
            logger.d("executeAction: $action")
            val evaluateContext = EvaluateContext.newBuilder()
                .setSrcId(action.id)
                .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                .setSrcTitle("Testing")
                .build()
            shortXManager.executeAction(
                context = evaluateContext,
                action = action.toProtoAction().pack_()
            )
        }
    }

    suspend fun executeAllAction() {
        withContext(Dispatchers.IO) {
            logger.d("executeAllAction")
            val protoActions = actions.map {
                it.toProtoAction().pack_()
            }
            val da = DirectAction.newBuilder()
                .setId("Tmp-${Date()}")
                .setLastUpdateTime(System.currentTimeMillis())
                .setTitle("Testing")
                .addAllActions(protoActions)
                .build()

            val evaluateContext = EvaluateContext.newBuilder()
                .setSrcId(da.id)
                .setSrcEvent(ContextSrcEvent.ContextSrc_ExecuteOnDAEditor)
                .setSrcTitle(da.title)
                .build()

            shortXManager.executeDirectionAction(context = evaluateContext, da = da)
        }
    }
}
