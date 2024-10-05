@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIcon
import tornaco.apps.shortx.ui.base.MDIconSelectorActivity
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class ShowStatusBarChipSelectorActivity : BaseSelectorActivity<Action.ShowStatusBarChip>() {
    @Composable
    override fun BaseSelectorActivity<Action.ShowStatusBarChip>.SelectorContent(currentData: Action.ShowStatusBarChip?) {
        ShowStatusBarChipSelector(currentData)
    }
}

@Composable
private fun ShowStatusBarChipSelectorActivity.ShowStatusBarChipSelector(currentAction: Action.ShowStatusBarChip?) {
    val vm = viewModel<ShowStatusBarChipSelectorViewModel>()
    LaunchedEffect(vm) {
        currentAction?.let { vm.initFrom(it) }
    }
    val state by vm.state.collectAsState()

    val actionHandler = remember(vm) { vm.clickActionHandler }
    val actionState by actionHandler.state.collectAsState()

    val longActionHandler = remember(vm) { vm.longClickActionHandler }
    val longActionState by longActionHandler.state.collectAsState()

    ShortXTheme {
        Scaffold(modifier = Modifier,
            topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(LocalI18N.current["ui.action.show.statusbar.clip"]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            cancelAndFinish()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            setResultAndFinish(
                                Action.ShowStatusBarChip(
                                    text = state.text,
                                    icon = state.icon,
                                    clickActions = actionState.actions,
                                    longClickActions = longActionState.actions
                                )
                            )
                        }) {
                            RemixIcon(remixName = Remix.System.check_line)
                        }
                    },
                )
            }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues)
                    .animateContentSize()
                    .verticalScroll(rememberScrollState())
            ) {

                ShortXTextField(modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = LocalI18N.current["ui.action.show.statusbar.clip.text"])
                    },
                    value = state.text,
                    onValueChange = {
                        vm.updateText(it)
                    })
                StandardSpacer()
                StandardSpacer()

                val iconLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = { result ->
                        val icon = result.popupSelectorResult<MDIcon>()
                        icon?.let {
                            vm.updateIcon("remix://${it.iconNameOrBase64}")
                        }
                    }
                )
                val context = LocalContext.current
                TipRow(text = "e.g. \napp://com.app.pkg\nremix://icon")
                SmallSpacer()
                ShortXTextField(modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = LocalI18N.current["ui.action.show.statusbar.clip.icon"])
                    },
                    value = state.icon,
                    onValueChange = {
                        vm.updateIcon(it)
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            iconLauncher.launch(
                                BaseSelectorActivity.newIntent(
                                    context,
                                    null,
                                    MDIconSelectorActivity::class.java
                                )
                            )
                        }) {
                            RemixIcon(remixName = Remix.Logos.remixicon_fill)
                        }
                    })
                LargeSpacer()

                tornaco.apps.shortx.ui.addrule.action.ActionContent(
                    header = {
                        SmallTitle(
                            modifier = Modifier.padding(vertical = 12.dp),
                            LocalI18N.current["ui.actions.single.click.title"]
                        )
                    },
                    actions = actionState.actions,
                    evaluatingActions = state.evaluatingActions,
                    handler = actionHandler.asActionHandler(),
                    warnings = actionState.actionWarningMessages
                )
                LargeSpacer()

                tornaco.apps.shortx.ui.addrule.action.ActionContent(
                    header = {
                        SmallTitle(
                            modifier = Modifier.padding(vertical = 12.dp),
                            LocalI18N.current["ui.actions.long.click.title"]
                        )
                    },
                    actions = longActionState.actions,
                    evaluatingActions = state.evaluatingActions,
                    handler = longActionHandler.asActionHandler(),
                    warnings = longActionState.actionWarningMessages
                )
            }
        }
    }
}

