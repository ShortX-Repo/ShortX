package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.ClipboardDialog
import tornaco.apps.shortx.ui.UiClipboard
import tornaco.apps.shortx.ui.addrule.ContextDataIcon
import tornaco.apps.shortx.ui.addrule.ContextDataInfoDialog
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelForFact
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.contextData
import tornaco.apps.shortx.ui.addrule.fact.selector.FactSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberFactSelectorDialogState
import tornaco.apps.shortx.ui.addrule.rememberContextDataInfoDialogState
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.VerticalDivider
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.rememberClipboardDialogState

class FactHandler(
    val addFact: (Fact) -> Unit,
    val removeFact: (Fact) -> Unit,
    val insertAbove: (fact: Fact, anchorId: String) -> Unit = { _, _ -> run {} },
    val insertBelow: (fact: Fact, anchorId: String) -> Unit = { _, _ -> run {} },
    val copy: (factId: String) -> Unit = {},
    val enableDisable: (factId: String) -> Unit = {},
)

@Composable
fun FactSelectors(
    header: @Composable ColumnScope.() -> Unit = {
        MediumTitle(LocalI18N.current["ui.fact.title"])
    },
    state: FactContentState,
    handler: FactHandler,
    readOnly: Boolean = false,
) {
    val factSelectorState = rememberFactSelectorDialogState<String>(onSelected = { fact, _ ->
        handler.addFact(fact)
    })
    FactSelectorDialog(state = factSelectorState)

    Column(modifier = Modifier.fillMaxWidth()) {
        header()

        ShortXCard(
            modifier = Modifier,
            shape = ShortXCardRoundedCornerShapeXL,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
            ) {
                state.facts.forEach { fact ->
                    FactItem(
                        fact = fact,
                        readOnly = readOnly,
                        handler = handler,
                    )
                }

                StandardSpacer()
                if (!readOnly) {
                    Button(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            factSelectorState.show()
                        }) {
                        RemixIcon(
                            modifier = Modifier.size(16.dp),
                            remixName = Remix.System.add_line,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        TinySpacer()
                        Text(text = LocalI18N.current["ui.fact.select"])
                    }
                }
            }
        }
    }
}


@Composable
private fun FactItem(
    fact: Fact,
    readOnly: Boolean,
    handler: FactHandler
) {
    val currentFact by rememberUpdatedState(newValue = fact)
    val factHandlers = listOf(
        appFactSelectHandler { newFact ->
            handler.addFact(newFact.clone(id = currentFact.id, note = currentFact.note))
        },
        systemFactSelectHandler { newFact ->
            composeLogger.d("systemFactSelectHandler: $newFact")
            handler.addFact(newFact.clone(id = currentFact.id, note = currentFact.note).apply {
                composeLogger.d("systemFactSelectHandler: $this")
            })
        }
    )

    val contextDataInfoDialogState = rememberContextDataInfoDialogState()
    ContextDataInfoDialog(state = contextDataInfoDialogState)
    val i18N = LocalI18N.current

    Box(modifier = Modifier) {
        val clickDropdownState = rememberDropdownSelectorState()

        ListItem(
            modifier = Modifier,
            titleLines = mutableListOf<String>().apply {
                addAll(labelForFact(fact = fact))
                onDebugBuild { add(currentFact.id) }
            },
            onClick = {
                factHandlers.any {
                    it(FactSelectSource.FromFact(currentFact))
                }
            },
            onLongClick = {
                clickDropdownState.open()
            },
            icon = {
                FactIcon(fact) {
                    clickDropdownState.open()
                }
            },
            action1 = {
                if (fact.contextData.isNotEmpty()) {
                    IconButton(onClick = {
                        contextDataInfoDialogState.show(
                            labelAndDescriptionForFactSelector(i18N, fact::class).first,
                            fact.contextData
                        )
                    }) {
                        ContextDataIcon()
                    }
                    VerticalDivider(modifier = Modifier.height(24.dp))
                }

                if (!readOnly) {
                    IconButton(onClick = {
                        handler.removeFact(currentFact)
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                }
            }
        )

        val clipboardDialogState = rememberClipboardDialogState()
        ClipboardDialog(state = clipboardDialogState)

        DropdownSelector(
            state = clickDropdownState,
            items = listOf(
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.paste.above"]),
                    data = currentFact.id to "pa",
                    icon = {
                        RemixIcon(
                            remixName = Remix.Editor.insert_row_top,
                            tint = colorForFact(fact::class)
                        )
                    }
                ),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.paste.below"]),
                    data = currentFact.id to "pb",
                    icon = {
                        RemixIcon(
                            remixName = Remix.Editor.insert_row_bottom,
                            tint = colorForFact(fact::class)
                        )
                    }
                ),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.copy"]),
                    data = currentFact.id to "cp",
                    icon = {
                        RemixIcon(
                            remixName = Remix.Document.file_copy_fill,
                            tint = colorForFact(fact::class)
                        )
                    }
                ),
            ),
            onSelect = { dropdownItem ->
                when (dropdownItem.data.second) {
                    "pa" -> {
                        val clipboardFacts = UiClipboard.getFacts()
                        if (clipboardFacts.size == 1) {
                            handler.insertAbove(
                                clipboardFacts[0].fact,
                                dropdownItem.data.first
                            )
                        } else {
                            clipboardDialogState.show { contentList ->
                                contentList.filterIsInstance<UiClipboard.ClipboardContent.FactContent>()
                                    .forEach {
                                        handler.insertAbove(
                                            it.fact,
                                            dropdownItem.data.first
                                        )
                                    }
                            }
                        }
                    }

                    "pb" -> {
                        val clipboardFacts = UiClipboard.getFacts()
                        if (clipboardFacts.size == 1) {
                            handler.insertBelow(
                                clipboardFacts[0].fact,
                                dropdownItem.data.first
                            )
                        } else {
                            clipboardDialogState.show { contentList ->
                                contentList.filterIsInstance<UiClipboard.ClipboardContent.FactContent>()
                                    .forEach {
                                        handler.insertBelow(
                                            it.fact,
                                            dropdownItem.data.first
                                        )
                                    }
                            }
                        }
                    }

                    "cp" -> {
                        handler.copy(dropdownItem.data.first)
                    }
                }
            })
    }
}

@Composable
fun FactIcon(fact: Fact, onClick: (() -> Unit)? = null) {
    ColoredIcon(
        size = 36.dp,
        color = colorForFact(fact::class).copy(
            ActionColorBgAlpha
        ),
        onClick = onClick
    ) {
        RemixIcon(
            modifier = Modifier.size(16.dp),
            remixName = imageVectorForFact(fact::class),
            tint = colorForFact(fact::class)
        )
    }
}