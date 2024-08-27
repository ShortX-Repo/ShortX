@file:OptIn(ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.addrule.fact

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.context.ContextDataMapping.asAccessExample
import tornaco.apps.shortx.core.context.GenericContextData
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
import tornaco.apps.shortx.ui.addrule.fact.selector.FactSelectorActivity
import tornaco.apps.shortx.ui.addrule.fact.selector.FactSelectorActivity.Companion.toFactOrNull
import tornaco.apps.shortx.ui.addrule.rememberContextDataInfoDialogState
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MD3Badge
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.NoteRow
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.TipDialog
import tornaco.apps.shortx.ui.base.VerticalDivider
import tornaco.apps.shortx.ui.base.clickableWithRipple
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.base.rememberTipDialogState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.rememberClipboardDialogState
import tornaco.apps.shortx.ui.theme.ErrorRed

class FactHandler(
    val addFact: (Fact) -> Unit,
    val removeFact: (Fact) -> Unit,
    val insertAbove: (fact: Fact, anchorId: String) -> Unit = { _, _ -> run {} },
    val insertBelow: (fact: Fact, anchorId: String) -> Unit = { _, _ -> run {} },
    val copy: (factId: String) -> Unit = {},
    val enableDisable: (factId: String) -> Unit = {},
    val setNote: (factId: String, note: String) -> Unit = { _, _ -> run {} },
    val setTag: (factId: String, note: String) -> Unit = { _, _ -> run {} },
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
    val context = LocalContext.current
    val factSelectActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            val fact = it.toFactOrNull()
            composeLogger.d("factSelectActivityLauncher, onResult: $it $fact")
            fact?.let { handler.addFact(fact) }
        }
    )

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
                            factSelectActivityLauncher.launch(FactSelectorActivity.newIntent(context))
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
            handler.addFact(
                newFact.clone(
                    id = currentFact.id,
                    note = currentFact.note,
                    tag = currentFact.tag,
                    isDisabled = currentFact.isDisabled,
                    customContextDataKey = currentFact.customContextDataKey
                )
            )
        },
        systemFactSelectHandler { newFact ->
            composeLogger.d("systemFactSelectHandler: $newFact")
            handler.addFact(
                newFact.clone(
                    id = currentFact.id,
                    note = currentFact.note,
                    tag = currentFact.tag,
                    isDisabled = currentFact.isDisabled,
                    customContextDataKey = currentFact.customContextDataKey
                ).apply {
                    composeLogger.d("systemFactSelectHandler: $this")
                })
        },
        sensorFactSelectHandler { newFact ->
            composeLogger.d("sensorFactSelectHandler: $newFact")
            handler.addFact(
                newFact.clone(
                    id = currentFact.id,
                    note = currentFact.note,
                    tag = currentFact.tag,
                    isDisabled = currentFact.isDisabled,
                    customContextDataKey = currentFact.customContextDataKey
                ).apply {
                    composeLogger.d("sensorFactSelectHandler: $this")
                })
        },
        miscFactSelectHandler { newFact ->
            handler.addFact(
                newFact.clone(
                    id = currentFact.id,
                    note = currentFact.note,
                    tag = currentFact.tag,
                    isDisabled = currentFact.isDisabled,
                    customContextDataKey = currentFact.customContextDataKey
                )
            )
        }
    )

    val contextDataInfoDialogState = rememberContextDataInfoDialogState()
    ContextDataInfoDialog(state = contextDataInfoDialogState)
    val i18N = LocalI18N.current

    Box(modifier = Modifier) {
        val clickDropdownState = rememberDropdownSelectorState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ShortXCardRoundedCornerShape)
                .combinedClickable(
                    enabled = !readOnly,
                    onClick = {
                        factHandlers.any {
                            it(FactSelectSource.FromFact(currentFact))
                        }
                    },
                    onLongClick = {
                        clickDropdownState.open()
                    }
                )
        ) {
            ListItem(
                modifier = Modifier,
                titleLines = mutableListOf<String>().apply {
                    addAll(labelForFact(fact = fact))
                    onDebugBuild { add(currentFact.id) }
                },
                textDecoration = if (currentFact.isDisabled.not()) TextDecoration.None else TextDecoration.LineThrough,
                titleColor = if (currentFact.isDisabled.not()) Color.Unspecified else ErrorRed,
                textColor = if (currentFact.isDisabled.not()) Color.Unspecified else ErrorRed,
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

            if (fact.tag.isNotBlank()) {
                val factTagContextDataDialogState =
                    rememberTipDialogState(
                        title = fact.tag,
                        textToCopy = GenericContextData.CONTEXT_FACT_TAG.asAccessExample(),
                        tip = LocalI18N.current[LocalI18N.current["ui.context.data.fact.tag", mapOf(
                            "eg" to GenericContextData.CONTEXT_FACT_TAG.asAccessExample()
                        )]]
                    )
                TipDialog(state = factTagContextDataDialogState)
                MD3Badge(modifier = Modifier
                    .padding(start = 68.dp, end = 16.dp)
                    .clip(ShortXCardRoundedCornerShape)
                    .clickableWithRipple {
                        factTagContextDataDialogState.show()
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RemixIcon(
                                modifier = Modifier.size(16.dp),
                                remixName = Remix.Finance.price_tag_3_line
                            )
                            SmallSpacer()
                            TipBody(text = fact.tag)
                        }
                    })
            }

            if (fact.note.isNotBlank()) {
                NoteRow(
                    modifier = Modifier
                        .padding(start = 68.dp, end = 16.dp)
                        .padding(bottom = 16.dp),
                    text = fact.note
                )
            }
        }

        val clipboardDialogState = rememberClipboardDialogState()
        ClipboardDialog(state = clipboardDialogState)

        val noteDialogState =
            rememberTextInputState(
                title = LocalI18N.current["ui.common.note"],
                message = null,
                inputValidator = {
                    InputValidateResult.OK
                }
            ) {
                handler.setNote(currentFact.id, it)
            }
        TextInputDialog(state = noteDialogState)

        val tagDialogState =
            rememberTextInputState(
                title = LocalI18N.current["ui.common.set.tag"],
                message = null,
                inputValidator = {
                    InputValidateResult.OK
                }
            ) {
                handler.setTag(currentFact.id, it)
            }
        TextInputDialog(state = tagDialogState)

        DropdownSelector(
            state = clickDropdownState,
            items = listOf(
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.paste.above"]),
                    data = currentFact.id to FactItemOptions.PasteAbove,
                    icon = {
                        RemixIcon(
                            remixName = Remix.Editor.insert_row_top,
                            tint = colorForFact(fact::class)
                        )
                    }
                ),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.paste.below"]),
                    data = currentFact.id to FactItemOptions.PasteBelow,
                    icon = {
                        RemixIcon(
                            remixName = Remix.Editor.insert_row_bottom,
                            tint = colorForFact(fact::class)
                        )
                    }
                ),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.copy"]),
                    data = currentFact.id to FactItemOptions.Copy,
                    icon = {
                        RemixIcon(
                            remixName = Remix.Document.file_copy_fill,
                            tint = colorForFact(fact::class)
                        )
                    }
                ),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.note"]),
                    data = currentFact.id to FactItemOptions.Note,
                    icon = {
                        RemixIcon(
                            remixName = Remix.Document.sticky_note_line,
                            tint = colorForFact(fact::class)
                        )
                    }
                ),
                DropdownItem(
                    labelLines = listOf(LocalI18N.current["ui.common.set.tag"]),
                    data = currentFact.id to FactItemOptions.SetTag,
                    icon = {
                        RemixIcon(
                            remixName = Remix.Finance.price_tag_3_line,
                            tint = colorForFact(fact::class)
                        )
                    }
                ),
                DropdownItem(
                    labelLines = listOf(
                        LocalI18N.current["ui.common.toggle.enable.disable"],
                        LocalI18N.current["ui.common.toggle.enable.disable.summary"]
                    ),
                    data = currentFact.id to FactItemOptions.EnabledDisabled,
                    icon = {
                        RemixIcon(
                            remixName = Remix.System.toggle_line,
                            tint = colorForFact(fact::class)
                        )
                    }
                ),
            ),
            onSelect = { dropdownItem ->
                when (dropdownItem.data.second) {
                    FactItemOptions.PasteAbove -> {
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

                    FactItemOptions.PasteBelow -> {
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

                    FactItemOptions.Copy -> {
                        handler.copy(dropdownItem.data.first)
                    }

                    FactItemOptions.SetTag -> {
                        tagDialogState.show(fact.tag)
                    }

                    FactItemOptions.Note -> {
                        noteDialogState.show(fact.note)
                    }

                    FactItemOptions.EnabledDisabled -> {
                        handler.enableDisable(dropdownItem.data.first)
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

enum class FactItemOptions {
    PasteAbove, PasteBelow, Copy, SetTag, Note, EnabledDisabled
}