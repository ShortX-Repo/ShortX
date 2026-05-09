@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package tornaco.apps.shortx.ui.flow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.common.KeyGesture
import tornaco.apps.shortx.core.proto.common.TimeUnit
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppComponents
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.base.ExpandIndicator
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.StandardSpacer

private enum class InstructionFlowNodeKind {
    Root,
    Section,
    Trigger,
    Condition,
    Action,
    Reference,
}

private data class InstructionFlowEdge(
    val label: String? = null,
    val node: InstructionFlowNode,
)

private data class InstructionFlowNode(
    val title: String,
    val detail: String? = null,
    val kind: InstructionFlowNodeKind,
    val children: List<InstructionFlowEdge> = emptyList(),
)

private data class InstructionReferenceState(
    val directActionIds: Set<String> = emptySet(),
    val functionIds: Set<String> = emptySet(),
)

private data class FlowExpansionCommand(
    val expand: Boolean,
    val token: Int,
)

private const val RootFlowPath = "root"

private val FlowConnectorWidth = 24.dp
private val FlowConnectorGap = 8.dp

data class RuleFlowSpec(
    val title: String,
    val parameters: List<FuncParameter>,
    val facts: List<Fact>,
    val conditions: List<Condition>,
    val actions: List<Action>,
    val actionsOnEnabled: List<Action>,
    val actionsOnDisabled: List<Action>,
    val actionsOnDeleted: List<Action>,
    val actionsOnUpdated: List<Action>,
    val actionsOnStopped: List<Action>,
    val quitEnabled: Boolean,
    val quitFacts: List<Fact>,
    val quitConditions: List<Condition>,
    val quitActions: List<Action>,
)

data class DirectActionFlowSpec(
    val title: String,
    val parameters: List<FuncParameter>,
    val actions: List<Action>,
    val actionsOnStop: List<Action>,
    val quitEnabled: Boolean,
    val quitFacts: List<Fact>,
    val quitConditions: List<Condition>,
    val quitActions: List<Action>,
)

private class InstructionFlowBuilder(
    private val shortXManager: ShortXManager,
    private val i18n: I18N,
) {
    fun buildRule(spec: RuleFlowSpec): InstructionFlowNode {
        val children = mutableListOf<InstructionFlowEdge>()
        addParameters(children, spec.parameters)
        addFacts(children, i18n["ui.flow.triggers"], spec.facts)
        addConditions(children, i18n["ui.flow.conditions"], spec.conditions)
        addActions(children, i18n["ui.flow.main.actions"], spec.actions, InstructionReferenceState())

        val hookChildren = listOfNotNull(
            buildActionSection(i18n["ui.flow.on.enable"], spec.actionsOnEnabled, InstructionReferenceState()),
            buildActionSection(i18n["ui.flow.on.disable"], spec.actionsOnDisabled, InstructionReferenceState()),
            buildActionSection(i18n["ui.flow.on.delete"], spec.actionsOnDeleted, InstructionReferenceState()),
            buildActionSection(i18n["ui.flow.on.update"], spec.actionsOnUpdated, InstructionReferenceState()),
            buildActionSection(i18n["ui.flow.on.stop"], spec.actionsOnStopped, InstructionReferenceState()),
        )
        if (hookChildren.isNotEmpty()) {
            children += InstructionFlowEdge(
                node = InstructionFlowNode(
                    title = i18n["ui.flow.hooks"],
                    kind = InstructionFlowNodeKind.Section,
                    children = hookChildren.map { InstructionFlowEdge(node = it) }
                )
            )
        }

        if (spec.quitEnabled) {
            val quitChildren = mutableListOf<InstructionFlowEdge>()
            addFacts(quitChildren, i18n["ui.flow.triggers"], spec.quitFacts)
            addConditions(quitChildren, i18n["ui.flow.conditions"], spec.quitConditions)
            addActions(quitChildren, i18n["ui.flow.actions"], spec.quitActions, InstructionReferenceState())
            if (quitChildren.isNotEmpty()) {
                children += InstructionFlowEdge(
                    node = InstructionFlowNode(
                        title = i18n["ui.flow.quit"],
                        kind = InstructionFlowNodeKind.Section,
                        children = quitChildren
                    )
                )
            }
        }

        return InstructionFlowNode(
            title = spec.title,
            detail = i18n["ui.settings.action.record.src.contextsrc_rule"],
            kind = InstructionFlowNodeKind.Root,
            children = children
        )
    }

    fun buildDirectAction(
        spec: DirectActionFlowSpec,
        state: InstructionReferenceState = InstructionReferenceState(),
    ): InstructionFlowNode {
        val children = mutableListOf<InstructionFlowEdge>()
        addParameters(children, spec.parameters)
        addActions(children, i18n["ui.flow.main.actions"], spec.actions, state)

        buildActionSection(i18n["ui.flow.on.stop"], spec.actionsOnStop, state)?.let {
            children += InstructionFlowEdge(node = it)
        }

        if (spec.quitEnabled) {
            val quitChildren = mutableListOf<InstructionFlowEdge>()
            addFacts(quitChildren, i18n["ui.flow.triggers"], spec.quitFacts)
            addConditions(quitChildren, i18n["ui.flow.conditions"], spec.quitConditions)
            addActions(quitChildren, i18n["ui.flow.actions"], spec.quitActions, state)
            if (quitChildren.isNotEmpty()) {
                children += InstructionFlowEdge(
                    node = InstructionFlowNode(
                        title = i18n["ui.flow.quit"],
                        kind = InstructionFlowNodeKind.Section,
                        children = quitChildren
                    )
                )
            }
        }

        return InstructionFlowNode(
            title = spec.title,
            detail = i18n["ui.settings.action.record.src.contextsrc_directaction"],
            kind = InstructionFlowNodeKind.Root,
            children = children
        )
    }

    private fun addParameters(
        target: MutableList<InstructionFlowEdge>,
        parameters: List<FuncParameter>,
    ) {
        if (parameters.isEmpty()) return
        target += InstructionFlowEdge(
            node = InstructionFlowNode(
                title = i18n["ui.flow.parameters"],
                kind = InstructionFlowNodeKind.Section,
                children = parameters.mapIndexed { index, parameter ->
                    InstructionFlowEdge(
                        label = (index + 1).toString(),
                        node = InstructionFlowNode(
                            title = parameter.name,
                            detail = buildString {
                                parameter.defaultValue.takeIf { it.isNotBlank() }?.let {
                                    append("default=")
                                    append(it)
                                }
                                if (parameter.isRequired) {
                                    if (isNotEmpty()) append(" • ")
                                    append("required")
                                }
                                parameter.comments.takeIf { it.isNotBlank() }?.let {
                                    if (isNotEmpty()) append(" • ")
                                    append(it)
                                }
                            }.ifBlank { null },
                            kind = InstructionFlowNodeKind.Action,
                        )
                    )
                }
            )
        )
    }

    private fun addFacts(
        target: MutableList<InstructionFlowEdge>,
        title: String,
        facts: List<Fact>,
    ) {
        if (facts.isEmpty()) return
        target += InstructionFlowEdge(
            node = InstructionFlowNode(
                title = title,
                kind = InstructionFlowNodeKind.Section,
                children = facts.mapIndexed { index, fact ->
                    val labelAndDescription = labelAndDescriptionForFactSelector(i18n, fact::class)
                    InstructionFlowEdge(
                        label = (index + 1).toString(),
                        node = InstructionFlowNode(
                            title = labelAndDescription.first,
                            detail = buildFactDetail(fact, labelAndDescription.second),
                            kind = InstructionFlowNodeKind.Trigger,
                        )
                    )
                }
            )
        )
    }

    private fun addConditions(
        target: MutableList<InstructionFlowEdge>,
        title: String,
        conditions: List<Condition>,
    ) {
        if (conditions.isEmpty()) return
        target += InstructionFlowEdge(
            node = InstructionFlowNode(
                title = title,
                kind = InstructionFlowNodeKind.Section,
                children = conditions.mapIndexed { index, condition ->
                    val label = labelForConditionSelector(i18n, condition::class).joinToString(" / ")
                    InstructionFlowEdge(
                        label = (index + 1).toString(),
                        node = InstructionFlowNode(
                            title = label,
                            detail = buildConditionDetail(condition),
                            kind = InstructionFlowNodeKind.Condition,
                        )
                    )
                }
            )
        )
    }

    private fun addActions(
        target: MutableList<InstructionFlowEdge>,
        title: String,
        actions: List<Action>,
        state: InstructionReferenceState,
    ) {
        buildActionSection(title, actions, state)?.let {
            target += InstructionFlowEdge(node = it)
        }
    }

    private fun buildActionSection(
        title: String,
        actions: List<Action>,
        state: InstructionReferenceState,
    ): InstructionFlowNode? {
        if (actions.isEmpty()) return null
        return InstructionFlowNode(
            title = title,
            kind = InstructionFlowNodeKind.Section,
            children = actions.mapIndexed { index, action ->
                InstructionFlowEdge(
                    label = (index + 1).toString(),
                    node = buildActionNode(action, state)
                )
            }
        )
    }

    private fun buildActionNode(
        action: Action,
        state: InstructionReferenceState,
    ): InstructionFlowNode {
        val children = mutableListOf<InstructionFlowEdge>()

        when (action) {
            is Action.IfThenElse -> {
                addConditions(children, i18n["ui.flow.conditions"], action.ifConditions)
                buildActionSection(i18n["ui.flow.then"], action.ifActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
                buildActionSection(i18n["ui.flow.else"], action.elseActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.SwitchCase -> {
                action.cases.forEachIndexed { index, case ->
                    val caseChildren = mutableListOf<InstructionFlowEdge>()
                    addConditions(caseChildren, i18n["ui.flow.conditions"], case.conditions)
                    addActions(caseChildren, i18n["ui.flow.actions"], case.actions, state)
                    children += InstructionFlowEdge(
                        label = "${i18n["ui.flow.case"]} ${index + 1}",
                        node = InstructionFlowNode(
                            title = case.description.ifBlank { "${i18n["ui.flow.case"]} ${index + 1}" },
                            detail = if (case.isBreak) "break" else null,
                            kind = InstructionFlowNodeKind.Section,
                            children = caseChildren
                        )
                    )
                }
                if (action.deft.actions.isNotEmpty()) {
                    children += InstructionFlowEdge(
                        node = InstructionFlowNode(
                            title = i18n["ui.flow.default"],
                            kind = InstructionFlowNodeKind.Section,
                            children = action.deft.actions.mapIndexed { index, childAction ->
                                InstructionFlowEdge(
                                    label = (index + 1).toString(),
                                    node = buildActionNode(childAction, state)
                                )
                            }
                        )
                    )
                }
            }

            is Action.WhileLoop -> {
                addConditions(children, i18n["ui.flow.conditions"], action.conditions)
                buildActionSection(i18n["ui.flow.body"], action.actions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.ForEach -> {
                buildActionSection(i18n["ui.flow.body"], action.actions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.ForEachPkgSet -> {
                buildActionSection(i18n["ui.flow.body"], action.actions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.ShowAlertDialog -> {
                buildActionSection(i18n["ui.flow.positive"], action.positiveActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
                buildActionSection(i18n["ui.flow.negative"], action.negativeActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
                buildActionSection(i18n["ui.flow.neutral"], action.neutralActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.PostNotification -> {
                buildActionSection(i18n["ui.flow.on.click"], action.onClickActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.ShowPPN -> {
                buildActionSection(i18n["ui.flow.on.click"], action.onClickActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.ShowStatusBarChip -> {
                buildActionSection(i18n["ui.flow.on.click"], action.clickActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
                buildActionSection(i18n["ui.flow.on.long.click"], action.longClickActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.BiometricVerify -> {
                buildActionSection(i18n["ui.flow.allow"], action.allowActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
                buildActionSection(i18n["ui.flow.deny"], action.denyActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.WebSocketConnect -> {
                buildActionSection(i18n["ui.flow.on.open"], action.openActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
                buildActionSection(i18n["ui.flow.on.closed"], action.closeActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
                buildActionSection(i18n["ui.flow.on.message"], action.messageActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
                buildActionSection(i18n["ui.flow.on.failure"], action.failureActions, state)?.let {
                    children += InstructionFlowEdge(node = it)
                }
            }

            is Action.WaitUtilConditionMatch -> {
                addConditions(children, i18n["ui.flow.conditions"], action.conditions)
                if (action.isQuitEnabled) {
                    val quitChildren = mutableListOf<InstructionFlowEdge>()
                    addFacts(quitChildren, i18n["ui.flow.triggers"], action.quitFacts)
                    addConditions(quitChildren, i18n["ui.flow.conditions"], action.quitConditions)
                    children += InstructionFlowEdge(
                        node = InstructionFlowNode(
                            title = i18n["ui.flow.quit"],
                            kind = InstructionFlowNodeKind.Section,
                            children = quitChildren
                        )
                    )
                }
            }

            is Action.FromDA -> {
                val daId = action.daId
                val referenced = shortXManager.getDirectActionById(daId)
                if (referenced != null && !state.directActionIds.contains(daId)) {
                    val nextState = state.copy(directActionIds = state.directActionIds + daId)
                    val refActions = referenced.actionsList.mapNotNull { it.toAction(shortXManager) }
                    val refQuit = referenced.quit
                    val refQuitFacts = refQuit.factsList.mapNotNull { it.toFact(shortXManager) }
                    val refQuitConditions = refQuit.conditionsList.mapNotNull { it.toCondition(shortXManager) }
                    val refQuitActions = refQuit.actionsList.mapNotNull { it.toAction(shortXManager) }
                    val refStopActions = referenced.hook?.actionsOnStopList?.mapNotNull { it.toAction(shortXManager) }.orEmpty()
                    children += InstructionFlowEdge(
                        label = i18n["ui.flow.reference"],
                        node = buildDirectAction(
                            DirectActionFlowSpec(
                                title = referenced.title,
                                parameters = referenced.parametersList,
                                actions = refActions,
                                actionsOnStop = refStopActions,
                                quitEnabled = refQuit.isEnabled,
                                quitFacts = refQuitFacts,
                                quitConditions = refQuitConditions,
                                quitActions = refQuitActions,
                            ),
                            nextState
                        ).copy(kind = InstructionFlowNodeKind.Reference)
                    )
                }
            }

            is Action.ExecuteFunction -> {
                val functionId = action.functionId
                val function = shortXManager.getFunctionById(functionId)
                if (function != null && !state.functionIds.contains(functionId)) {
                    val nextState = state.copy(functionIds = state.functionIds + functionId)
                    val functionActions = function.actionsList.mapNotNull { it.toAction(shortXManager) }
                    children += InstructionFlowEdge(
                        label = i18n["ui.flow.reference"],
                        node = InstructionFlowNode(
                            title = function.name,
                            detail = i18n["ui.function.title"],
                            kind = InstructionFlowNodeKind.Reference,
                            children = functionActions.mapIndexed { index, childAction ->
                                InstructionFlowEdge(
                                    label = (index + 1).toString(),
                                    node = buildActionNode(childAction, nextState)
                                )
                            }
                        )
                    )
                }
            }

            else -> Unit
        }

        val detailParts = mutableListOf<String>()
        labelAndDescriptionForActionSelector(i18n, action::class).second?.let { detailParts += it }
        action.note.takeIf { it.isNotBlank() }?.let { detailParts += it }
        actionSummary(action)?.let { detailParts += it }
        if (!action.isEnabled) {
            detailParts += "disabled"
        }

        return InstructionFlowNode(
            title = labelAndDescriptionForActionSelector(i18n, action::class).first,
            detail = detailParts.joinToString(" • ").ifBlank { null },
            kind = if (action is Action.FromDA || action is Action.ExecuteFunction) {
                InstructionFlowNodeKind.Reference
            } else {
                InstructionFlowNodeKind.Action
            },
            children = children
        )
    }

    private fun actionSummary(action: Action): String? {
        return when (action) {
            is Action.ShellCommand -> summarizeText(action.command)
            is Action.InputText -> summarizeText(action.text)
            is Action.ShowToast -> summarizeText(action.message)
            is Action.WriteClipboard -> summarizeText(action.text)
            is Action.Delay -> "${action.time} ${labelForTimeUnit(action.timeUnit)}"
            is Action.MVEL -> summarizeScript(action.expression, action.context.name)
            is Action.ExecuteJS -> summarizeScript(action.expression, action.context.name)
            is Action.LaunchApp -> summarizeApps(listOf(action.app))
            is Action.LaunchAppByPkg -> summarizeStringPairs(action.stringPairs)
            is Action.StopApp -> summarizeApps(action.apps, action.pkgSets)
            is Action.StopAppByPkg -> summarizeStringPairs(action.stringPairs)
            is Action.StartActivity -> summarizeText(action.componentNameAsString)
            is Action.StartActivityIntent -> summarizeText(action.data ?: action.action ?: action.pkgName ?: action.className)
            is Action.StartActivityIntentUri -> summarizeText(action.intentUri)
            is Action.StartActivityUrlSchema -> summarizeText(action.urlSchema)
            is Action.WhileLoop -> "repeat=${action.repeat}, delay=${action.delay}"
            is Action.ForEach -> action.data.takeIf { it.isNotBlank() }
            is Action.ForEachPkgSet -> action.pkgSet.label.takeIf { it.isNotBlank() }
            is Action.FromDA -> action.title ?: action.daId
            is Action.ExecuteFunction -> action.name ?: action.functionId
            is Action.ShowAlertDialog -> action.title.takeIf { it.isNotBlank() }
            is Action.ShowFloatWindow -> summarizeFloatingUi(action.tag, action.settings.size, "radius=${action.panelRadiusScale}")
            is Action.ShowSideBar -> summarizeFloatingUi(action.tag, action.settings.size)
            is Action.HideFloatWindow -> summarizeTags(action.tags)
            is Action.HideSideBar -> summarizeTags(action.tags)
            is Action.ShowOverlay -> summarizeOverlay(action)
            is Action.PostNotification -> summarizePostedNotification(action)
            is Action.RemoveNotification -> summarizeNotification(action.n)
            is Action.ShowPPN -> action.title.takeIf { it.isNotBlank() }
            is Action.WebSocketConnect -> action.url.takeIf { it.isNotBlank() }
            else -> null
        }
    }

    private fun buildFactDetail(fact: Fact, mapperDescription: String?): String? {
        val detailParts = mutableListOf<String>()
        mapperDescription?.takeIf { it.isNotBlank() }?.let { detailParts += it }
        when (fact) {
            is Fact.KeyEvent -> detailParts += "keyCode=${fact.keyCode}"
            is Fact.CombineKeyEvent -> detailParts += "${fact.keyCode1} + ${fact.keyCode2}"
            is Fact.AdvancedKeyEvent -> {
                detailParts += "keyCode=${fact.keyCode}"
                detailParts += labelForKeyGesture(fact.gesture)
                if (fact.isInterceptMode) detailParts += "intercept"
            }
            is Fact.ActivityStarted -> summarizeComponents(fact.components)?.let { detailParts += it }
            is Fact.ActivityStopped -> summarizeComponents(fact.components)?.let { detailParts += it }
            is Fact.ActivityDestroyed -> summarizeComponents(fact.components)?.let { detailParts += it }
            is Fact.AppBecomeForeground -> summarizeApps(fact.apps, fact.pkgSets)?.let { detailParts += it }
            is Fact.AppBecomeBackground -> summarizeApps(fact.apps, fact.pkgSets)?.let { detailParts += it }
            is Fact.WifiConnectedTo -> summarizeLabels(fact.ssidList)?.let { detailParts += it }
            is Fact.WifiDisconnectedFrom -> summarizeLabels(fact.ssidList)?.let { detailParts += it }
            is Fact.NotificationPosted -> summarizeNotification(fact.n)?.let { detailParts += it }
            is Fact.NotificationUpdated -> summarizeNotification(fact.n)?.let { detailParts += it }
            is Fact.NotificationRemoved -> summarizeNotification(fact.n)?.let { detailParts += it }
            is Fact.AppProcessStarted -> summarizeProcessNames(fact.processNames)?.let { detailParts += it }
            is Fact.AppProcessRemoved -> summarizeProcessNames(fact.processNames)?.let { detailParts += it }
            is Fact.Alarm -> detailParts += "${fact.timeOfADay.hour}:${fact.timeOfADay.minutes.toString().padStart(2, '0')}"
            is Fact.FixedInPeriod -> detailParts += "${fact.start.hour}:${fact.start.minutes.toString().padStart(2, '0')} - ${fact.end.hour}:${fact.end.minutes.toString().padStart(2, '0')}"
            is Fact.RandomInPeriod -> detailParts += "${fact.start.hour}:${fact.start.minutes.toString().padStart(2, '0')} - ${fact.end.hour}:${fact.end.minutes.toString().padStart(2, '0')}"
            is Fact.EdgeGesture -> detailParts += listOfNotNull(
                fact.edge.name,
                fact.gesture.name,
                fact.childSteps.takeIf { it.isNotEmpty() }?.joinToString(" -> "),
            ).joinToString(" • ")
            is Fact.ScreenGesture -> detailParts += fact.gesture.name
            is Fact.Broadcast -> summarizeLabels(fact.actions)?.let { detailParts += it }
            else -> Unit
        }
        fact.note.takeIf { it.isNotBlank() }?.let { detailParts += it }
        fact.tag.takeIf { it.isNotBlank() }?.let { detailParts += "tag=${it}" }
        if (fact.isDisabled) detailParts += "disabled"
        return detailParts.joinToString(" • ").ifBlank { null }
    }

    private fun buildConditionDetail(condition: Condition): String? {
        val detailParts = mutableListOf<String>()
        when (condition) {
            is Condition.CurrentForegroundApp -> summarizeApps(condition.apps, condition.pkgSets)?.let { detailParts += it }
            is Condition.AppHasNotification -> summarizeApps(condition.apps, condition.pkgSets)?.let { detailParts += it }
            is Condition.AppHasTask -> summarizeApps(condition.apps, condition.pkgSets)?.let { detailParts += it }
            is Condition.CurrentForegroundAppByPkg -> summarizeStringPairs(condition.stringPairs)?.let { detailParts += it }
            is Condition.AppHasTaskByPkg -> summarizeStringPairs(condition.stringPairs)?.let { detailParts += it }
            is Condition.RequireAPMMode -> detailParts += booleanState(condition.isEnableAPM)
            is Condition.RequireIMEVisibility -> detailParts += booleanState(condition.isShown)
            is Condition.RequireNotificationPanelExpanded -> detailParts += booleanState(condition.isExpand)
            is Condition.BatteryPercent -> detailParts += "${condition.op.name} ${condition.value}%"
            is Condition.AvailableMemory -> detailParts += "${condition.op.name} ${condition.valueMb}MB"
            is Condition.ChargeState -> detailParts += booleanState(condition.requireIsCharge)
            is Condition.PlugState -> detailParts += condition.type.name
            is Condition.ScreenIsOn -> detailParts += booleanState(true)
            is Condition.VPNIsConnected -> detailParts += booleanState(true)
            is Condition.TimeInRange -> detailParts += condition.range.toString()
            is Condition.CalendarDayMatch -> detailParts += condition.dayType.name
            is Condition.ConnectedWifiSignal -> detailParts += "${condition.op.name} ${condition.level}"
            is Condition.RequireWifiConnected -> summarizeText(condition.requiredSSID)?.let { detailParts += it }
            is Condition.RequireWifiDisconnected -> detailParts += booleanState(true)
            is Condition.RequireBTConnected -> summarizeText(condition.requiredDevice)?.let { detailParts += it }
            is Condition.RequireBTDisconnected -> detailParts += booleanState(true)
            is Condition.RequireBTDeviceFound -> summarizeLabels(listOfNotNull(condition.device, condition.address, condition.timeout))?.let { detailParts += it }
            is Condition.RequireMobileDataEnabled -> detailParts += booleanState(true)
            is Condition.KeyguardIsLocked -> detailParts += booleanState(true)
            is Condition.ScreenOrientationIsPort -> detailParts += booleanState(true)
            is Condition.IsInCall -> detailParts += booleanState(true)
            is Condition.IsRinging -> detailParts += booleanState(true)
            else -> Unit
        }
        condition.note.takeIf { it.isNotBlank() }?.let { detailParts += it }
        if (condition.isInvert) detailParts += "inverted"
        if (condition.isDisabled) detailParts += "disabled"
        return detailParts.joinToString(" • ").ifBlank { null }
    }

    private fun summarizeApps(
        apps: List<tornaco.apps.shortx.core.proto.common.App>,
        pkgSets: List<tornaco.apps.shortx.core.proto.pkgset.PkgSet> = emptyList(),
    ): String? {
        return mutableListOf<String>()
            .labelLinesFromAppsAndPkgSets(apps, pkgSets)
            .map { it.substringBefore('$').trim() }
            .let { summarizeLabels(it) }
    }

    private fun summarizeComponents(components: List<tornaco.apps.shortx.core.proto.common.AppComponent>): String? {
        return mutableListOf<String>()
            .labelLinesFromAppComponents(components)
            .map { it.substringBefore('$').trim() }
            .let { summarizeLabels(it) }
    }

    private fun summarizeStringPairs(pairs: List<tornaco.apps.shortx.core.proto.common.StringPair>): String? {
        val labels = pairs.map { it.second }.filter { it.isNotBlank() }
        return summarizeLabels(labels)
    }

    private fun summarizeNotification(notification: Fact.Notification): String? {
        val labels = mutableListOf<String>()
        summarizeText(notification.titleRegex)?.let { labels += it }
        summarizeText(notification.contentRegex)?.let { labels += it }
        return summarizeLabels(labels)
    }

    private fun summarizePostedNotification(action: Action.PostNotification): String? {
        val labels = mutableListOf<String>()
        summarizeText(action.title)?.let { labels += it }
        summarizeText(action.message)?.let { labels += it }
        action.overrideAppName.takeIf { it.isNotBlank() }?.let { labels += it }
        if (action.isImportant) labels += "important"
        if (action.ongoing) labels += "ongoing"
        if (action.buttons.isNotEmpty()) labels += "buttons=${action.buttons.size}"
        if (action.onClickActions.isNotEmpty()) labels += "clickActions=${action.onClickActions.size}"
        return summarizeLabels(labels)
    }

    private fun summarizeFloatingUi(tag: String, buttonCount: Int, extra: String? = null): String? {
        val labels = mutableListOf<String>()
        tag.takeIf { it.isNotBlank() }?.let { labels += "tag=$it" }
        labels += "buttons=$buttonCount"
        extra?.takeIf { it.isNotBlank() }?.let { labels += it }
        return summarizeLabels(labels)
    }

    private fun summarizeOverlay(action: Action.ShowOverlay): String? {
        val labels = mutableListOf<String>()
        action.tag.takeIf { it.isNotBlank() }?.let { labels += "tag=$it" }
        labels += "buttons=${action.settings.size}"
        labels += "${action.maxWidth}x${action.maxHeight}"
        if (action.autoCollapse) labels += "autoCollapse"
        if (action.enableGlobalDrag) labels += "draggable"
        if (action.closeOnTouchOutside) labels += "outsideClose"
        if (action.closeOnAction) labels += "closeOnAction"
        return summarizeLabels(labels)
    }

    private fun summarizeTags(tags: List<String>): String? {
        val labels = tags.filter { it.isNotBlank() }.map { "tag=$it" }
        return summarizeLabels(labels)
    }

    private fun summarizeScript(expression: String, context: String): String? {
        val labels = mutableListOf<String>()
        summarizeText(expression)?.let { labels += it }
        context.takeIf { it.isNotBlank() }?.let { labels += "ctx=$it" }
        return summarizeLabels(labels)
    }

    private fun summarizeProcessNames(processNames: List<tornaco.apps.shortx.core.proto.common.ProcessName>): String? {
        val labels = processNames.map { it.processName }.filter { it.isNotBlank() }
        return summarizeLabels(labels)
    }

    private fun summarizeLabels(labels: List<String>): String? {
        if (labels.isEmpty()) return null
        val shown = labels.take(3)
        val suffix = if (labels.size > shown.size) " +${labels.size - shown.size}" else ""
        return shown.joinToString(", ") + suffix
    }

    private fun summarizeText(text: String?): String? {
        val clean = text?.trim().orEmpty()
        if (clean.isBlank()) return null
        return if (clean.length > 48) clean.take(45) + "..." else clean
    }

    private fun booleanState(value: Boolean): String = if (value) "true" else "false"

    private fun labelForKeyGesture(gesture: KeyGesture): String {
        return when (gesture) {
            KeyGesture.KeyPress -> "press"
            KeyGesture.KeyLongPress -> "long press"
            KeyGesture.KeyMultiplePress -> "multi press"
            KeyGesture.KeyTriplePress -> "triple press"
            KeyGesture.UNRECOGNIZED -> "unknown"
        }
    }

    private fun labelForTimeUnit(timeUnit: TimeUnit): String {
        return when (timeUnit) {
            TimeUnit.TimeUnit_H -> "hour"
            TimeUnit.TimeUnit_M -> "minute"
            TimeUnit.TimeUnit_S -> "second"
            TimeUnit.TimeUnit_MS -> "ms"
            TimeUnit.TimeUnit_D -> "day"
            TimeUnit.UNRECOGNIZED -> "time"
        }
    }
}

@Composable
fun InstructionFlowDialog(
    title: String,
    root: RuleFlowSpec?,
    shortXManager: ShortXManager,
    onDismissRequest: () -> Unit,
) {
    val i18n = LocalI18N.current
    val node = root?.let { InstructionFlowBuilder(shortXManager, i18n).buildRule(it) } ?: return
    InstructionFlowDialogContent(title = title, root = node, onDismissRequest = onDismissRequest)
}

@Composable
fun InstructionFlowDialog(
    title: String,
    root: DirectActionFlowSpec?,
    shortXManager: ShortXManager,
    onDismissRequest: () -> Unit,
) {
    val i18n = LocalI18N.current
    val node = root?.let { InstructionFlowBuilder(shortXManager, i18n).buildDirectAction(it) } ?: return
    InstructionFlowDialogContent(title = title, root = node, onDismissRequest = onDismissRequest)
}

@Composable
private fun InstructionFlowDialogContent(
    title: String,
    root: InstructionFlowNode,
    onDismissRequest: () -> Unit,
) {
    val i18n = LocalI18N.current
    var expansionCommand by rememberSaveable { mutableStateOf<FlowExpansionCommand?>(null) }
    var pathFocusMode by rememberSaveable { mutableStateOf(false) }
    var focusedPath by rememberSaveable { mutableStateOf(RootFlowPath) }

    ShortFullScreenXDialog(onDismissRequest = onDismissRequest) {
        ShortXAppBarScaffold(
            title = title,
            verticalScroll = false,
            actions = {
                IconButton(
                    onClick = {
                        pathFocusMode = !pathFocusMode
                        focusedPath = RootFlowPath
                    }
                ) {
                    RemixIcon(
                        remixName = if (pathFocusMode) {
                            Remix.Design.focus_3_fill
                        } else {
                            Remix.Design.focus_3_line
                        },
                        tint = if (pathFocusMode) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                IconButton(
                    onClick = {
                        pathFocusMode = false
                        val nextToken = (expansionCommand?.token ?: 0) + 1
                        expansionCommand = FlowExpansionCommand(expand = true, token = nextToken)
                    }
                ) {
                    RemixIcon(remixName = Remix.Arrows.arrow_down_s_line)
                }
                IconButton(
                    onClick = {
                        pathFocusMode = false
                        val nextToken = (expansionCommand?.token ?: 0) + 1
                        expansionCommand = FlowExpansionCommand(expand = false, token = nextToken)
                    }
                ) {
                    RemixIcon(remixName = Remix.Arrows.arrow_up_s_line)
                }
            },
            onBackPressed = onDismissRequest,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                AnimatedVisibility(pathFocusMode) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = i18n["ui.flow.focus.mode.enabled"],
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            StandardSpacer()
                            Text(
                                text = i18n["ui.flow.focus.mode.tip"],
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }
                if (pathFocusMode) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                InstructionFlowNodeView(
                    node = root,
                    depth = 0,
                    edgeLabel = null,
                    nodePath = RootFlowPath,
                    pathFocusMode = pathFocusMode,
                    focusedPath = focusedPath,
                    onPathFocusRequested = { focusedPath = it },
                    expansionCommand = expansionCommand,
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun InstructionFlowNodeView(
    node: InstructionFlowNode,
    depth: Int,
    edgeLabel: String?,
    nodePath: String,
    pathFocusMode: Boolean,
    focusedPath: String,
    onPathFocusRequested: (String) -> Unit,
    expansionCommand: FlowExpansionCommand?,
) {
    var expanded by rememberSaveable(node.title, node.detail, depth) {
        mutableStateOf(depth < 2 || node.kind == InstructionFlowNodeKind.Root)
    }
    val hasChildren = node.children.isNotEmpty()
    val isFocusedNode = focusedPath == nodePath
    val isOnFocusedPath = nodePath == RootFlowPath || focusedPath == nodePath || focusedPath.startsWith("$nodePath/")

    LaunchedEffect(expansionCommand?.token, pathFocusMode, focusedPath) {
        if (!hasChildren) return@LaunchedEffect
        if (pathFocusMode) {
            expanded = isOnFocusedPath
        } else if (expansionCommand != null) {
            expanded = expansionCommand.expand
        }
    }

    val shape = RoundedCornerShape(18.dp)
    val color = when (node.kind) {
        InstructionFlowNodeKind.Root -> MaterialTheme.colorScheme.primaryContainer
        InstructionFlowNodeKind.Section -> MaterialTheme.colorScheme.surfaceVariant
        InstructionFlowNodeKind.Trigger -> MaterialTheme.colorScheme.secondaryContainer
        InstructionFlowNodeKind.Condition -> MaterialTheme.colorScheme.tertiaryContainer
        InstructionFlowNodeKind.Reference -> MaterialTheme.colorScheme.errorContainer
        InstructionFlowNodeKind.Action -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when (node.kind) {
        InstructionFlowNodeKind.Root -> MaterialTheme.colorScheme.primary
        InstructionFlowNodeKind.Section -> MaterialTheme.colorScheme.outlineVariant
        InstructionFlowNodeKind.Trigger -> MaterialTheme.colorScheme.secondary
        InstructionFlowNodeKind.Condition -> MaterialTheme.colorScheme.tertiary
        InstructionFlowNodeKind.Reference -> MaterialTheme.colorScheme.error
        InstructionFlowNodeKind.Action -> MaterialTheme.colorScheme.outline
    }
    val highlightedColor = when {
        !pathFocusMode || !isOnFocusedPath -> color
        isFocusedNode -> MaterialTheme.colorScheme.primaryContainer
        else -> color.copy(alpha = 0.92f)
    }
    val highlightedBorderColor = when {
        !pathFocusMode || !isOnFocusedPath -> borderColor
        isFocusedNode -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    }
    val edgeColor = if (pathFocusMode && isOnFocusedPath) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val edgeTextColor = if (pathFocusMode && isOnFocusedPath) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (edgeLabel != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(2.dp)
                        .background(edgeColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = edgeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = edgeTextColor
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable(enabled = hasChildren) {
                    if (pathFocusMode) {
                        onPathFocusRequested(nodePath)
                    } else {
                        expanded = !expanded
                    }
                },
            shape = shape,
            color = highlightedColor,
            border = androidx.compose.foundation.BorderStroke(1.dp, highlightedBorderColor)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = node.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    node.detail?.takeIf { it.isNotBlank() }?.let {
                        StandardSpacer()
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                if (hasChildren) {
                    ExpandIndicator(isExpand = expanded) {
                        if (pathFocusMode) {
                            onPathFocusRequested(nodePath)
                        } else {
                            expanded = it
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !hasChildren || expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (hasChildren) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                node.children.forEachIndexed { index, child ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                    ) {
                        FlowConnector(
                            isLast = index == node.children.lastIndex,
                            color = if (pathFocusMode && pathIsFocused(focusedPath, "$nodePath/$index")) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                highlightedBorderColor
                            },
                        )
                        Spacer(modifier = Modifier.width(FlowConnectorGap))
                        Column(modifier = Modifier.weight(1f)) {
                            InstructionFlowNodeView(
                                node = child.node,
                                depth = depth + 1,
                                edgeLabel = child.label,
                                nodePath = "$nodePath/$index",
                                pathFocusMode = pathFocusMode,
                                focusedPath = focusedPath,
                                onPathFocusRequested = onPathFocusRequested,
                                expansionCommand = expansionCommand,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

private fun pathIsFocused(
    focusedPath: String,
    currentPath: String,
): Boolean {
    return focusedPath == currentPath || focusedPath.startsWith("$currentPath/")
}

@Composable
private fun FlowConnector(
    isLast: Boolean,
    color: Color,
) {
    Canvas(
        modifier = Modifier
            .width(FlowConnectorWidth)
            .height(72.dp)
    ) {
        val strokeWidth = 2.dp.toPx()
        val centerX = size.width * 0.45f
        val elbowY = size.height * 0.35f

        drawLine(
            color = color,
            start = Offset(centerX, 0f),
            end = Offset(centerX, elbowY),
            strokeWidth = strokeWidth,
        )
        drawLine(
            color = color,
            start = Offset(centerX, elbowY),
            end = Offset(size.width, elbowY),
            strokeWidth = strokeWidth,
        )
        if (!isLast) {
            drawLine(
                color = color,
                start = Offset(centerX, elbowY),
                end = Offset(centerX, size.height),
                strokeWidth = strokeWidth,
            )
        }
        drawCircle(
            color = color,
            radius = 3.dp.toPx(),
            center = Offset(centerX, elbowY),
            style = Stroke(width = strokeWidth)
        )
    }
}