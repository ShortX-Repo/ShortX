package tornaco.apps.shortx.ui.addrule.action

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
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
    val copiedAction: Action? = null,
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
        insertAbove = { action, anchor ->
            this.insertActionAbove(action, anchor)
        },
        insertBelow = { action, anchor ->
            this.insertActionBelow(action, anchor)
        },
        copy = { action ->
            this.copy(action)
        },
        pasteAbove = { anchor ->
            this.pasteActionAbove(anchor)
        },
        pasteBelow = { anchor ->
            this.pasteActionBelow(anchor)
        },
        removeAction = { action ->
            this.removeAction(action)
        },
        applySort = { sorted ->
            this.sortActions(sorted)
        },
        setNote = { action, note ->
            this.setNote(action, note)
        },
        execute = {
            this.executeAction(it)
        },
        executeAll = {
            this.executeAllAction()
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

    fun insertActionAbove(action: Action, anchor: Action) {
        updateState {
            copy(actions = actions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == anchor.id }
                if (existingIndex >= 0) {
                    add(existingIndex, action)
                } else {
                    add(action)
                }
            }, isChanged = true)
        }
        checkUpdateWarnings()
    }

    fun insertActionBelow(action: Action, anchor: Action) {
        updateState {
            copy(actions = actions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == anchor.id }
                if (existingIndex >= 0) {
                    add(existingIndex + 1, action)
                } else {
                    add(action)
                }
            }, isChanged = true)
        }
        checkUpdateWarnings()
    }

    fun copy(action: Action) {
        updateState {
            copy(copiedAction = action)
        }
    }

    fun pasteActionAbove(anchor: Action) {
        state.value.copiedAction?.let { copied ->
            val action = copied.toProtoAction(overrideId = defaultNewActionId()).pack_()
                .toAction(shortXManager)
            action?.let {
                updateState {
                    copy(actions = actions.toMutableList().apply {
                        val existingIndex = indexOfFirst { it.id == anchor.id }
                        if (existingIndex >= 0) {
                            add(existingIndex, it)
                        } else {
                            add(it)
                        }
                    }, isChanged = true)
                }
                checkUpdateWarnings()
            }
        }
    }

    fun pasteActionBelow(anchor: Action) {
        state.value.copiedAction?.let { copied ->
            val action = copied.toProtoAction(overrideId = defaultNewActionId()).pack_()
                .toAction(shortXManager)
            action?.let {
                updateState {
                    copy(actions = actions.toMutableList().apply {
                        val existingIndex = indexOfFirst { it.id == anchor.id }
                        if (existingIndex >= 0) {
                            add(existingIndex + 1, it)
                        } else {
                            add(it)
                        }
                    }, isChanged = true)
                }
                checkUpdateWarnings()
            }
        }
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
            shortXManager.executeAction(action.toProtoAction().pack_())
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
                .setTitle("Tmp")
                .addAllActions(protoActions)
                .build()

            shortXManager.executeDirectionAction(da)
        }
    }
}
