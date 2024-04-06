@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionIcon
import tornaco.apps.shortx.ui.addrule.action.mapper.labelLinesForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.ConditionIcon
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelForCondition
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.fact.FactIcon
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelForFact
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.thenIf
import tornaco.apps.shortx.ui.theme.ErrorRed
import tornaco.apps.shortx.ui.theme.ShortXTheme


object UiClipboard {
    sealed interface ClipboardContent {
        val id: String

        data class ActionContent(
            val action: Action,
            override val id: String = action.id
        ) : ClipboardContent

        data class ConditionContent(
            val condition: Condition,
            override val id: String = condition.id
        ) : ClipboardContent

        data class FactContent(
            val fact: Fact,
            override val id: String = fact.id
        ) : ClipboardContent
    }

    private val contents: MutableList<ClipboardContent> = mutableListOf()

    val all get() = contents.toList().reversed()

    fun getActions() = contents.filterIsInstance<ClipboardContent.ActionContent>()
    fun getFacts() = contents.filterIsInstance<ClipboardContent.FactContent>()
    fun getConditions() = contents.filterIsInstance<ClipboardContent.ConditionContent>()

    fun copy(clipboardContent: ClipboardContent) {
        remove(clipboardContent.id)
        this.contents.add(clipboardContent)
    }

    fun remove(id: String) {
        this.contents.removeIf { it.id == id }
    }

    fun removeAll() {
        this.contents.clear()
    }
}

class ClipboardDialogState : CommonDialogState() {
    var all by mutableStateOf<List<UiClipboard.ClipboardContent>>(emptyList())
    var onSelected: (List<UiClipboard.ClipboardContent>) -> Unit = {}

    var selections: List<UiClipboard.ClipboardContent> by mutableStateOf(emptyList())

    fun show(onSelected: (List<UiClipboard.ClipboardContent>) -> Unit) {
        all = UiClipboard.all
        this.onSelected = onSelected
        this.selections = emptyList()
        show()
    }

    override fun dismiss() {
        super.dismiss()
        this.onSelected = {}
    }

    fun select(content: UiClipboard.ClipboardContent) {
        this.selections = this.selections.toMutableList().apply {
            add(content)
        }
    }

    fun unSelect(content: UiClipboard.ClipboardContent) {
        this.selections = this.selections.toMutableList().apply {
            remove(content)
        }
    }

    fun remove(id: String) {
        all = all.toMutableList().apply {
            removeIf {
                it.id == id
            }
        }

        UiClipboard.remove(id)
    }

    fun removeAll() {
        all = emptyList()
        UiClipboard.removeAll()
    }
}

@Composable
fun rememberClipboardDialogState(): ClipboardDialogState {
    return remember {
        ClipboardDialogState()
    }
}

@Composable
fun ClipboardDialog(
    state: ClipboardDialogState,
) {
    if (state.isShowing) {
        ShortFullScreenXDialog(
            onDismissRequest = {
                state.dismiss()
            }
        ) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(LocalI18N.current["ui.title.ui.clipboard"]) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                },
                                actions = {
                                    AnimatedVisibility(visible = state.selections.isNotEmpty()) {
                                        TextButton(onClick = {
                                            state.onSelected(state.selections)
                                            state.dismiss()
                                        }) {
                                            Text(LocalI18N.current["ui.common.done"] + " ${state.selections.size}")
                                        }
                                    }
                                },
                            )
                        }) { paddingValues ->

                        val facts = remember(state.all) {
                            state.all.filterIsInstance<UiClipboard.ClipboardContent.FactContent>()
                        }
                        val conditions = remember(state.all) {
                            state.all.filterIsInstance<UiClipboard.ClipboardContent.ConditionContent>()
                        }
                        val actions = remember(state.all) {
                            state.all.filterIsInstance<UiClipboard.ClipboardContent.ActionContent>()
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            if (facts.isNotEmpty()) {
                                stickyHeader {
                                    FieldTitle(
                                        modifier = Modifier.padding(16.dp),
                                        text = LocalI18N.current["ui.fact.title"]
                                    )
                                }
                            }
                            items(facts) {
                                val fact = it.fact
                                ListItem(
                                    modifier = Modifier.thenIf(
                                        Modifier.background(MaterialTheme.colorScheme.primaryContainer),
                                        state.selections.contains(it)
                                    ),
                                    titleLines = mutableListOf<String>().apply {
                                        addAll(labelForFact(fact = fact))
                                    },
                                    onClick = {
                                        if (state.selections.contains(it)) {
                                            state.unSelect(it)
                                        } else {
                                            state.select(it)
                                        }
                                    },
                                    icon = {
                                        FactIcon(fact)
                                    },
                                    action1 = {
                                        IconButton(onClick = { state.remove(it.id) }) {
                                            RemixIcon(remixName = Remix.System.close_line)
                                        }
                                    }
                                )
                            }

                            if (conditions.isNotEmpty()) {
                                stickyHeader {
                                    FieldTitle(
                                        modifier = Modifier.padding(16.dp),
                                        text = LocalI18N.current["ui.conditions.title"]
                                    )
                                }
                            }
                            items(conditions) {
                                val condition = it.condition
                                ListItem(
                                    modifier = Modifier.thenIf(
                                        Modifier.background(MaterialTheme.colorScheme.primaryContainer),
                                        state.selections.contains(it)
                                    ),
                                    titleLines = mutableListOf<String>().apply {
                                        addAll(labelForCondition(condition = condition))
                                    },
                                    icon = {
                                        ConditionIcon(condition)
                                    },
                                    action1 = {
                                        IconButton(onClick = { state.remove(it.id) }) {
                                            RemixIcon(remixName = Remix.System.close_line)
                                        }
                                    },
                                    onClick = {
                                        if (state.selections.contains(it)) {
                                            state.unSelect(it)
                                        } else {
                                            state.select(it)
                                        }
                                    })
                            }

                            if (actions.isNotEmpty()) {
                                stickyHeader {
                                    FieldTitle(
                                        modifier = Modifier.padding(16.dp),
                                        text = LocalI18N.current["ui.actions.title"]
                                    )
                                }
                            }
                            items(actions) {
                                val action = it.action
                                ListItem(
                                    modifier = Modifier.thenIf(
                                        Modifier.background(MaterialTheme.colorScheme.primaryContainer),
                                        state.selections.contains(it)
                                    ),
                                    titleLines = mutableListOf<String>().apply {
                                        addAll(labelLinesForAction(action = action))
                                        onDebugBuild { add(action.id) }
                                    },
                                    textDecoration = if (action.isEnabled) TextDecoration.None else TextDecoration.LineThrough,
                                    titleColor = if (action.isEnabled) Color.Unspecified else ErrorRed,
                                    textColor = if (action.isEnabled) Color.Unspecified else ErrorRed,
                                    icon = {
                                        ActionIcon(action) {}
                                    },
                                    action1 = {
                                        IconButton(onClick = { state.remove(it.id) }) {
                                            RemixIcon(remixName = Remix.System.close_line)
                                        }
                                    },
                                    onClick = {
                                        if (state.selections.contains(it)) {
                                            state.unSelect(it)
                                        } else {
                                            state.select(it)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


