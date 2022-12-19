package tornaco.apps.shortx.ui.addrule.action


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.action.OverlayButtonSetting
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.addrule.models.toProtoAction
import tornaco.apps.shortx.ui.base.*

class ShowOverlayButtonSelectorState(
    val selectableActions: Map<I18NKey, List<Action>>,
    val onSelected: (List<OverlayButtonItem>) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberShowOverlayButtonSelectorState(
    selectableActions: Map<I18NKey, List<Action>>,
    onSelected: (List<OverlayButtonItem>) -> Unit
): ShowOverlayButtonSelectorState {
    return remember {
        ShowOverlayButtonSelectorState(selectableActions, onSelected)
    }
}

@Composable
fun ShowOverlayButtonSelector(state: ShowOverlayButtonSelectorState) {
    var overlayButtonItems by remember(state) {
        mutableStateOf(mutableListOf<OverlayButtonItem>())
    }
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = "ShowOverlayButton")
        }, buttons = {
            TextButton(onClick = {
                if (overlayButtonItems.any { it.actions.isNotEmpty() }) {
                    state.onSelected(overlayButtonItems)
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.68f)
                    .animateContentSize()
                    .verticalScroll(rememberScrollState())
            ) {
                overlayButtonItems.forEachIndexed { index, item ->
                    val iconSelector = rememberMDIconSelectorState {
                        overlayButtonItems = overlayButtonItems.toMutableList().apply {
                            val current = get(index)
                            set(index, current.copy(icon = it))
                        }
                    }
                    MDIconSelector(state = iconSelector)
                    Column {
                        MediumTitle(text = "Button of $index")

                        Row(verticalAlignment = CenterVertically) {
                            SmallTitle("Icon")
                            TinySpacer()
                            TextButton(onClick = {
                                iconSelector.show()
                            }) {
                                Icon(
                                    imageVector = MDIcons.filled(item.icon),
                                    contentDescription = item.icon
                                )
                            }
                        }

                        ActionSelectors(
                            header = {
                                SmallTitle(text = LocalI18N.current["ui.actions.title"])
                            },
                            selectableActions = state.selectableActions,
                            actions = overlayButtonItems[index].actions,
                            addAction = { action ->
                                overlayButtonItems = overlayButtonItems.toMutableList().apply {
                                    val current = get(index)
                                    set(
                                        index,
                                        current.copy(
                                            actions = current.actions.toMutableList().apply {
                                                add(action)
                                            }
                                        )
                                    )
                                }
                            },
                            removeAction = { action ->
                                overlayButtonItems = overlayButtonItems.toMutableList().apply {
                                    val current = get(index)
                                    set(
                                        index,
                                        current.copy(
                                            actions = current.actions.toMutableList().apply {
                                                remove(action)
                                            }
                                        )
                                    )
                                }
                            }
                        )
                    }

                    Divider(modifier = Modifier.fillMaxWidth())
                    StandardSpacer()
                }

                StandardSpacer()
                StandardSpacer()
                // Add icon
                FilledTonalButton(onClick = {
                    overlayButtonItems = overlayButtonItems.toMutableList().apply {
                        add(OverlayButtonItem())
                    }
                }) {
                    Text(text = "Add Button")
                }
            }
        })
    }
}


data class OverlayButtonItem(
    val icon: String = BuildProp.DEFAULT_OVERLAY_BUTTON_ICON,
    val actions: List<Action> = emptyList()
)

fun OverlayButtonItem.toProtoModel(): OverlayButtonSetting {
    return OverlayButtonSetting.newBuilder().setIcon(icon).addAllActions(
        actions.map { it.toProtoAction().pack_() }
    ).build()
}