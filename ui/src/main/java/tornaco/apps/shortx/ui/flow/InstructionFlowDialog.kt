@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package tornaco.apps.shortx.ui.flow

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.proto.rule.ActionAsyncMode
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForConditionSelector
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
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
                    InstructionFlowEdge(
                        label = (index + 1).toString(),
                        node = InstructionFlowNode(
                            title = labelAndDescriptionForFactSelector(i18n, fact::class).first,
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
                    InstructionFlowEdge(
                        label = (index + 1).toString(),
                        node = InstructionFlowNode(
                            title = labelForConditionSelector(i18n, condition::class).joinToString(" / "),
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
            is Action.WhileLoop -> "repeat=${action.repeat}, delay=${action.delay}"
            is Action.ForEach -> action.data.takeIf { it.isNotBlank() }
            is Action.ForEachPkgSet -> action.pkgSet.label.takeIf { it.isNotBlank() }
            is Action.FromDA -> action.title ?: action.daId
            is Action.ExecuteFunction -> action.name ?: action.functionId
            is Action.ShowAlertDialog -> action.title.takeIf { it.isNotBlank() }
            is Action.PostNotification -> action.title.takeIf { it.isNotBlank() }
            is Action.ShowPPN -> action.title.takeIf { it.isNotBlank() }
            is Action.WebSocketConnect -> action.url.takeIf { it.isNotBlank() }
            else -> null
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
    ShortFullScreenXDialog(onDismissRequest = onDismissRequest) {
        ShortXAppBarScaffold(
            title = title,
            verticalScroll = false,
            onBackPressed = onDismissRequest,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                InstructionFlowNodeView(node = root, depth = 0, edgeLabel = null)
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
) {
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

    Column(modifier = Modifier.fillMaxWidth()) {
        if (edgeLabel != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = (depth * 20).dp, bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = edgeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (depth * 20).dp),
            shape = shape,
            color = color,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
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
        }

        if (node.children.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
        }

        node.children.forEach { child ->
            InstructionFlowNodeView(
                node = child.node,
                depth = depth + 1,
                edgeLabel = child.label,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}