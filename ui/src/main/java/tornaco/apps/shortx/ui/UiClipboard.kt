@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionIcon
import tornaco.apps.shortx.ui.addrule.action.mapper.labelLinesForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.theme.ErrorRed
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.util.UUID


object UiClipboard {
    sealed interface ClipboardContent {
        val id: String

        data class ActionContent(
            val action: Action,
            override val id: String = UUID.randomUUID().toString()
        ) : ClipboardContent

        data class ConditionContent(
            val condition: Condition,
            override val id: String = UUID.randomUUID().toString()
        ) : ClipboardContent

        data class FactContent(
            val fact: Fact,
            override val id: String = UUID.randomUUID().toString()
        ) : ClipboardContent
    }

    private val contents: MutableList<ClipboardContent> = mutableListOf()

    val all get() = contents.toList()

    fun hasAction() = contents.any { it is ClipboardContent.ActionContent }
    fun getActions() = contents.filterIsInstance<ClipboardContent.ActionContent>()

    fun copy(clipboardContent: ClipboardContent) {
        this.contents.add(clipboardContent)
    }

    fun remove(id: String) {
        this.contents.removeIf { it.id == id }
    }
}

class ClipboardDialogState : CommonDialogState() {
    var all by mutableStateOf<List<UiClipboard.ClipboardContent>>(emptyList())

    override fun show() {
        all = UiClipboard.all
        super.show()
    }

    fun remove(id: String) {
        all = all.toMutableList().apply {
            removeIf {
                it.id == id
            }
        }

        UiClipboard.remove(id)
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
                                title = { Text(LocalI18N.current["ui.title.select.action"]) },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                },
                                actions = {
                                },
                            )
                        }) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                                .verticalScroll(rememberScrollState())
                        ) {
                            state.all.forEach { item ->
                                when (item) {
                                    is UiClipboard.ClipboardContent.ActionContent -> {
                                        val action = item.action
                                        ListItem(
                                            modifier = Modifier,
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
                                                IconButton(onClick = { state.remove(item.id) }) {
                                                    RemixIcon(remixName = Remix.System.close_line)
                                                }
                                            }
                                        )
                                    }

                                    is UiClipboard.ClipboardContent.ConditionContent -> {

                                    }

                                    is UiClipboard.ClipboardContent.FactContent -> {

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


