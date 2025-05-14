package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.mapper.displayNameKey
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.labelLinesFromAppsAndPkgSets
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState

data class EdgeGestureSelectDialogResult(
    val edge: Edge,
    val gesture: Gesture,
    val isIntercept: Boolean,
    val apps: List<App>,
    val pkgSet: List<PkgSet>,
)

class EdgeGestureSelectorState(
    val showEdgeHint: (Edge) -> Unit,
    val selected: (EdgeGestureSelectDialogResult) -> Unit
) : CommonDialogState() {
    var edge by mutableStateOf(Edge.BottomRight)
    var gesture by mutableStateOf(Gesture.DoubleTap)
    var isIntercept by mutableStateOf(false)

    val apps = mutableStateListOf<App>()
    val pkgSets = mutableStateListOf<PkgSet>()

    fun show(data: Fact.EdgeGesture?) {
        this.edge = data?.edge ?: Edge.BottomRight
        this.gesture = data?.gesture ?: Gesture.SingleTap
        this.isIntercept = data?.isIntercept ?: false
        this.apps.clear()
        this.pkgSets.clear()
        this.apps.addAll(data?.apps ?: emptyList())
        this.pkgSets.addAll(data?.pkgSets ?: emptyList())
        show()
    }
}

@Composable
fun rememberEdgeGestureSelectorState(
    showEdgeHint: (Edge) -> Unit,
    selected: (EdgeGestureSelectDialogResult) -> Unit
): EdgeGestureSelectorState {
    return remember {
        EdgeGestureSelectorState(showEdgeHint, selected)
    }
}

@Composable
fun EdgeGestureSelector(state: EdgeGestureSelectorState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.fact.edge.gesture"])
        }, buttons = {
            TextButton(onClick = {
                state.selected(
                    EdgeGestureSelectDialogResult(
                        state.edge, state.gesture, state.isIntercept, state.apps, state.pkgSets
                    )
                )
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val i18N = LocalI18N.current
                Box {
                    val dropdownState = rememberDropdownSelectorState()

                    Column {
                        SmallTitle(
                            modifier = Modifier.padding(vertical = 12.dp),
                            text = i18N["ui.fact.edge.gesture.position"]
                        )
                        ListItem(
                            title = i18N[state.edge.displayNameKey()],
                            action1 = {
                                IconButton(onClick = { dropdownState.open() }) {
                                    RemixIcon(remixName = Remix.Arrows.arrow_right_s_line)
                                }
                            },
                            onClick = {
                                dropdownState.open()
                            })
                    }

                    DropdownSelector(
                        state = dropdownState,
                        items = Edge.entries.filterNot { it == Edge.UNRECOGNIZED }.map {
                            DropdownItem(labelLines = listOf(i18N[it.displayNameKey()]), data = it)
                        },
                        onSelect = {
                            state.edge = it.data
                            state.showEdgeHint(it.data)
                        }
                    )
                }

                StandardSpacer()

                Box {
                    val dropdownState = rememberDropdownSelectorState()
                    Column {
                        SmallTitle(
                            modifier = Modifier.padding(vertical = 12.dp),
                            text = i18N["ui.fact.edge.gesture.gesture"]
                        )
                        ListItem(
                            title = i18N[state.gesture.displayNameKey()],
                            action1 = {
                                IconButton(onClick = { dropdownState.open() }) {
                                    RemixIcon(remixName = Remix.Arrows.arrow_right_s_line)
                                }
                            },
                            onClick = {
                                dropdownState.open()
                            })
                    }
                    DropdownSelector(
                        state = dropdownState,
                        items = Gesture.entries.filterNot { it == Gesture.UNRECOGNIZED }.map {
                            DropdownItem(labelLines = listOf(i18N[it.displayNameKey()]), data = it)
                        },
                        onSelect = {
                            state.gesture = it.data
                        }
                    )
                }

                StandardSpacer()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FieldTitle(text = LocalI18N.current["ui.fact.key.event.adv.is.intercept.mode"])
                    ShortXSwitch(checked = state.isIntercept, onCheckedChange = {
                        state.isIntercept = it
                    })
                }
                StandardSpacer()

                val appFgPkgSelectorState =
                    rememberAppPkgSelectorState(
                        onAppListSelected = { pkg ->
                            state.apps.clear()
                            state.pkgSets.clear()
                            state.apps.addAll(pkg)
                        }, onPkgSetSelected = {
                            state.apps.clear()
                            state.pkgSets.clear()
                            state.pkgSets.addAll(it)
                        })
                AppPkgSelectorDialog(appFgPkgSelectorState)

                val appPkgSetText = mutableListOf(
                    LocalI18N.current[
                        if (state.apps.isEmpty() && state.pkgSets.isEmpty()) {
                            "ui.fact.edge.gesture.front.app.any"
                        } else {
                            "ui.fact.edge.gesture.front.app"
                        }
                    ]
                ).labelLinesFromAppsAndPkgSets(
                    state.apps,
                    state.pkgSets
                )
                StandardSpacer()
                ListItem(titleLines = appPkgSetText, onClick = {
                    appFgPkgSelectorState.show(
                        state.apps,
                        state.pkgSets
                    )
                })
                LargeSpacer()

                AnimatedVisibility(visible = state.isIntercept) {
                    TipRow(text = LocalI18N.current["ui.fact.motion.event.adv.summary"])
                    StandardSpacer()
                    StandardSpacer()
                }
            }
        })
    }
}

