@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.condition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.base.AppListContent
import tornaco.apps.shortx.ui.addrule.base.BaseAppPkgSelectorDialogState
import tornaco.apps.shortx.ui.addrule.base.PkgSetContent
import tornaco.apps.shortx.ui.addrule.condition.mapper.opDescription
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.condition.selector.rememberAppAllAnyMenuDialogState
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogState
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXFilledTonalButton
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme

class ConditionAppPkgSelectorDialogState(
    val onAppListSelected: ((List<App>, op: Op) -> Unit)?,
    val onPkgSetSelected: ((List<PkgSet>, op: Op) -> Unit)?
) : BaseAppPkgSelectorDialogState() {
    private var _op by mutableStateOf<Op>(Op.Any)
    val op get() = _op

    fun selectOp(op: Op) {
        logger.d("selectOp: $op")
        _op = op
    }

    fun show(
        currentAppList: List<App>? = null,
        currentPkgSet: List<PkgSet>? = null,
        op: Op? = Op.All
    ) {
        logger.d("show: $op")
        _op = op ?: Op.Any
        show(currentAppList, currentPkgSet)
    }
}

@Composable
fun rememberConditionAppPkgSelectorState(
    onAppListSelected: ((List<App>, op: Op) -> Unit)? = null,
    onPkgSetSelected: ((List<PkgSet>, op: Op) -> Unit)? = null
): ConditionAppPkgSelectorDialogState {
    return remember {
        ConditionAppPkgSelectorDialogState(onAppListSelected, onPkgSetSelected)
    }
}

@Composable
fun ConditionAppPkgSelectorDialog(state: ConditionAppPkgSelectorDialogState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            AppPkgSelector(state)
        }
    }
}

@Composable
private fun AppPkgSelector(state: ConditionAppPkgSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            var selectedTabIndex by remember {
                mutableStateOf(0)
            }
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.dialog.title.select.app"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (selectedTabIndex == 0) {
                                    state.onAppListSelected?.invoke(state.selectedApps, state.op)
                                } else {
                                    state.onPkgSetSelected?.invoke(
                                        state.selectedPkgSets.filter {
                                            shortXManager.getPkgSetByLabel(it.label) != null
                                        },
                                        state.op
                                    )
                                }
                                state.dismiss()

                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AnimatedVisibility(
                        visible = (selectedTabIndex == 0 && state.selectedApps.size > 1)
                                || (selectedTabIndex == 1 && state.selectedPkgSets.isNotEmpty())
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            MediumTitle(
                                text = LocalI18N.current["ui.condition.selector.app.all.any.op.title"]
                            )
                            MediumSpacer()
                            val opSelectorDialogState: MenuDialogState<String> =
                                rememberAppAllAnyMenuDialogState(
                                    onItemSelected = { data, op ->
                                        state.selectOp(op)
                                    })
                            MenuDialog(state = opSelectorDialogState)
                            ShortXFilledTonalButton(onClick = {
                                opSelectorDialogState.show()
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = opDescription(state.op, LocalI18N.current)[0])
                                    StandardSpacer()
                                    RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                                }
                            }
                            LargeSpacer()
                        }
                    }

                    TabRow(selectedTabIndex = selectedTabIndex) {
                        val titles = mutableListOf(
                            LocalI18N.current["ui.dialog.title.select.app"],
                            LocalI18N.current["ui.pkg.set.title"]
                        )
                        titles.forEachIndexed { index, title ->
                            Tab(selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                                    )
                                })
                        }
                    }

                    if (selectedTabIndex == 0) {
                        AppListContent(state)
                    } else {
                        PkgSetContent(state)
                    }


                }
            }
        }
    }
}
