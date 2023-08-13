@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme


class SetRuleEnabledDialogState(
    val onSelected: (Action.SetRuleEnabled) -> Unit
) : CommonDialogState() {
    var currentRule: Rule? by mutableStateOf(null)
    var isEnable: Boolean by mutableStateOf(false)

    fun show(action: Action.SetRuleEnabled? = null) {
        this.currentRule = null
        this.currentRule = action?.let { shortXManager.getRuleById(it.ruleId) }
        this.isEnable = action?.enableRule ?: false

        super.show()
    }
}

@Composable
fun rememberSetRuleEnabledDialogState(
    onSelected: (Action.SetRuleEnabled) -> Unit
): SetRuleEnabledDialogState {
    return remember {
        SetRuleEnabledDialogState(onSelected)
    }
}

@Composable
fun SetRuleEnabledSelectorDialog(state: SetRuleEnabledDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            SetRuleEnabledSelector(state)
        }
    }
}

@Composable
private fun SetRuleEnabledSelector(dialogState: SetRuleEnabledDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.set.rule.enabled"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                val current = dialogState.currentRule
                                if (current != null) {
                                    dialogState.onSelected(
                                        Action.SetRuleEnabled(
                                            ruleId = current.id,
                                            ruleLabel = current.title,
                                            enableRule = dialogState.isEnable
                                        )
                                    )
                                    dialogState.dismiss()
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->
                val ruleSelectDialog = rememberRuleSelectDialogState(onSelected = {
                    dialogState.currentRule = it
                })
                RuleSelectSelectorDialog(state = ruleSelectDialog)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    FieldTitle(text = LocalI18N.current["ui.action.set.rule.enabled.select.rule"])
                    SmallSpacer()
                    ListItem(
                        title = dialogState.currentRule?.title
                            ?: LocalI18N.current["ui.common.unselected"],
                        onClick = {
                            ruleSelectDialog.show()
                        })
                    StandardSpacer()


                    FieldTitle(text = LocalI18N.current["ui.action.set.rule.enabled.toggle"])
                    SmallSpacer()
                    Switch(checked = dialogState.isEnable, onCheckedChange = {
                        dialogState.isEnable = it
                    })
                }
            }
        }
    }

}