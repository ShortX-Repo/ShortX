package tornaco.apps.shortx.ui.addrule.action

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.addrule.condition.getGlobalVarRefs
import tornaco.apps.shortx.ui.addrule.logger
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.WithState
import tornaco.apps.shortx.ui.base.WithStateImpl
import tornaco.apps.shortx.ui.base.i18NIns

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
        }
    )
}

class ActionContentStateHandler(
    private val withState: WithStateImpl<ActionContentState> = WithStateImpl(ActionContentState())
) : WithState<ActionContentState> by withState {
    private val shortXManager = tornaco.apps.shortx.core.shortXManager
    private val logger = Logger("ActionContentStateHandler")
    private val i18N = i18NIns

    val actions get() = state.value.actions

    fun addAction(action: Action) {
        logger.d("addAction: ${action.id}")
        updateState {
            copy(actions = actions.toMutableList().apply {
                val existingIndex = indexOfFirst { it.id == action.id }
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
            it.value.generateWarningMessages(
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
}

fun List<Action>.generateWarningMessages(shortXManager: ShortXManager, i18N: I18N): List<String> {
    return getGlobalVarRefs(shortXManager)
        .apply { logger.d("getGlobalVarRefs: $this") }
        .filter { shortXManager.getGlobalVarByName(it) == null }
        .mapIndexed { index, it ->
            "${index + 1}. " + i18N["ui.condition.warning.message.global.var.missing", mapOf("name" to it)]
        }
}

fun List<Action>.getGlobalVarRefs(shortXManager: ShortXManager): List<String> {
    val writeGV =
        filterIsInstance(Action.WriteGV::class.java).map { it.varName }

    val ifElseCondition = filterIsInstance(Action.IfThenElse::class.java).map {
        it.ifConditions.getGlobalVarRefs()
    }.flatten()

    val ifElseAction = filterIsInstance(Action.IfThenElse::class.java).map {
        it.ifActions.getGlobalVarRefs(shortXManager) + it.elseActions.getGlobalVarRefs(shortXManager)
    }.flatten()

    val showDialogAction = filterIsInstance(Action.ShowAlertDialog::class.java).map {
        it.positiveActions.getGlobalVarRefs(shortXManager) + it.negativeActions.getGlobalVarRefs(
            shortXManager
        ) + it.negativeActions.getGlobalVarRefs(shortXManager)
    }.flatten()

    val showOverlayAction = filterIsInstance<Action.ShowOverlay>().map { overlay ->
        overlay.settings.map { buttonSetting ->
            buttonSetting.actionsList.mapNotNull {
                it.toAction(shortXManager)
            }
        }.map { it.getGlobalVarRefs(shortXManager) }.flatten()
    }.flatten()

    val fromDA = filterIsInstance<Action.FromDA>().mapNotNull { fromDA ->
        shortXManager.getDirectActionById(fromDA.daId)?.let { directAction ->
            directAction.actionsList.mapNotNull { it.toAction(shortXManager) }
        }?.getGlobalVarRefs(shortXManager)
    }.flatten()

    return writeGV + ifElseCondition + ifElseAction + showDialogAction + showOverlayAction + fromDA
}
