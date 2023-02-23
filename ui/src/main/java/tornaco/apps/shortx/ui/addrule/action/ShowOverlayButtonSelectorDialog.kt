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
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.*

class ShowOverlayButtonSelectorState(
    val installedApps: List<App>, val onSelected: (ShowOverlayButtonSettings) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    private var _initialData: ShowOverlayButtonSettings by mutableStateOf(
        ShowOverlayButtonSettings(
            false, emptyList()
        )
    )
    val initialData: ShowOverlayButtonSettings get() = _initialData

    fun show(initialData: ShowOverlayButtonSettings) {
        _initialData = initialData
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberShowOverlayButtonSelectorState(
    installedApps: List<App>, onSelected: (ShowOverlayButtonSettings) -> Unit
): ShowOverlayButtonSelectorState {
    return remember {
        ShowOverlayButtonSelectorState(
            installedApps = installedApps, onSelected = onSelected
        )
    }
}

@Composable
fun ShowOverlayButtonSelector(state: ShowOverlayButtonSelectorState) {
    if (state.isShow) {
        var overlayButtonItems by remember(state) {
            mutableStateOf(state.initialData.items)
        }
        var isAutoCollapse by remember(state.initialData) {
            mutableStateOf(state.initialData.autoCollapse)
        }
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = "ShowOverlayButton")
        }, buttons = {
            TextButton(onClick = {
                if (overlayButtonItems.any { it.actions.isNotEmpty() }) {
                    state.onSelected(
                        ShowOverlayButtonSettings(
                            autoCollapse = isAutoCollapse, items = overlayButtonItems
                        )
                    )
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
                        MediumTitle(text = LocalI18N.current["ui.action.show.button.of", mapOf("index" to "${index + 1}")])

                        Row(verticalAlignment = CenterVertically) {
                            SmallTitle(LocalI18N.current["ui.action.show.icon"])
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
                            actions = overlayButtonItems[index].actions, addAction = { action ->
                                overlayButtonItems = overlayButtonItems.toMutableList().apply {
                                    val current = get(index)
                                    set(
                                        index,
                                        current.copy(
                                            actions = current.actions.toMutableList().apply {
                                                add(action)
                                            })
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
                                            })
                                    )
                                }
                            },
                            installedApps = state.installedApps
                        )
                    }

                    StandardSpacer()
                    Divider(modifier = Modifier.fillMaxWidth())
                    StandardSpacer()
                }

                Row(verticalAlignment = CenterVertically) {
                    SmallTitle(text = LocalI18N.current["ui.action.show.auto.collapse"])

                    StandardSpacer()

                    Switch(checked = isAutoCollapse, onCheckedChange = {
                        isAutoCollapse = it
                    })
                }


                StandardSpacer()

                // Add icon
                FilledTonalButton(onClick = {
                    overlayButtonItems = overlayButtonItems.toMutableList().apply {
                        add(OverlayButtonItem())
                    }
                }) {
                    Text(text = LocalI18N.current["ui.action.show.add.button"])
                }
            }
        })
    }
}


data class ShowOverlayButtonSettings(val autoCollapse: Boolean, val items: List<OverlayButtonItem>)

data class OverlayButtonItem(
    val icon: String = BuildProp.DEFAULT_OVERLAY_BUTTON_ICON,
    val actions: List<Action> = emptyList()
)

fun OverlayButtonItem.toOverlayButtonSetting(): OverlayButtonSetting {
    return OverlayButtonSetting.newBuilder().setIcon(icon)
        .addAllActions(actions.map { it.toProtoAction().pack_() }).build()
}

fun OverlayButtonSetting.toOverlayButtonItem(): OverlayButtonItem {
    return OverlayButtonItem(icon = icon, actions = actionsList.mapNotNull {
        it.toAction(shortXManager = shortXManager)
    })
}