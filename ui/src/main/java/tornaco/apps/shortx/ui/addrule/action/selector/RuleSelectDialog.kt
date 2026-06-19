@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.dynamicStaggeredGridCells
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R


class RuleSelectDialogState(
    val onSelected: (Rule) -> Unit
) : CommonDialogState() {
}

@Composable
fun rememberRuleSelectDialogState(
    onSelected: (Rule) -> Unit
): RuleSelectDialogState {
    return remember {
        RuleSelectDialogState(onSelected)
    }
}

@Composable
fun RuleSelectSelectorDialog(state: RuleSelectDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            RuleSelectSelector(state)
        }
    }
}

@Composable
private fun RuleSelectSelector(dialogState: RuleSelectDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current[I18N_R.UI_ACTION_SET_RULE_ENABLED_SELECT_RULE]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                        },
                    )
                }) { paddingValues ->

                val allRules = remember {
                    shortXManager.getAllRules()
                }

                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = dynamicStaggeredGridCells(),
                    contentPadding = paddingValues
                ) {
                    items(allRules) {
                        ShortXCard(modifier = Modifier.padding(16.dp)) {
                            ListItem(title = it.title,
                                text1 = it.description,
                                onClick = {
                                    dialogState.onSelected(it)
                                    dialogState.dismiss()
                                })
                        }
                    }
                }
            }
        }
    }

}