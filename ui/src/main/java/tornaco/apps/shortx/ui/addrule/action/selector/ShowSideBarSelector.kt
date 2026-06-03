package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.ActionContent
import tornaco.apps.shortx.ui.addrule.action.selector.ActionThemeModeSelector
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIcon
import tornaco.apps.shortx.ui.base.MDIconSelectorActivity
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry

@AndroidEntryPoint
class ShowSideBarSelectorActivity : BaseSelectorActivity<Action.ShowSideBar>() {
    @Composable
    override fun BaseSelectorActivity<Action.ShowSideBar>.SelectorContent(currentData: Action.ShowSideBar?) {
        ShowSideBarButtonSelector(currentData)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<Action.ShowSideBar>.ShowSideBarButtonSelector(currentAction: Action.ShowSideBar?) {
    val vm = hiltViewModel<ShowSideBarSelectorViewModel>()
    LaunchedEffect(vm) {
        if (currentAction == null) {
            vm.resetState()
        } else {
            vm.initFrom(currentAction)
        }
    }
    val selectorState by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.show.side.bar"],
        onBackPressed = {
            cancelAndFinish()
        },
        actions = {
            IconButton(onClick = {
                val action = selectorState.toAction()
                setResultAndFinish(action)
            }) {
                RemixIcon(remixName = Remix.System.check_line)
            }

        },
    ) {
        Column(Modifier.padding(16.dp)) {
            Content(vm = vm)
        }
    }
}

@Composable
private fun ColumnScope.Content(
    vm: ShowSideBarSelectorViewModel
) {
    val state by vm.state.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = SpaceBetween
    ) {
        FieldTitle(text = LocalI18N.current["ui.action.show.add.button"])
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                vm.setButtonListExpanded(!state.isButtonListExpanded)
            }) {
                RemixIcon(remixName = Remix.Arrows.expand_up_down_fill)
            }
        }
    }
    SmallSpacer()
    AnimatedVisibility(visible = state.isButtonListExpanded) {
        Column {
            state.buttons.forEachIndexed { index, button ->
                ButtonListItem(
                    index = index,
                    button = button,
                    evaluatingActions = state.evaluatingActions,
                    updateIcon = { i: Int, s: String ->
                        vm.updateIcon(i, s)
                    },
                    removeButton = {
                        vm.removeButton(it)
                    })
            }
        }
    }

    StandardSpacer()
    // Add icon
    Button(
        modifier = Modifier.fillMaxSize(),
        onClick = {
            vm.addButton()
        }) {
        Text(text = LocalI18N.current["ui.action.show.add.button"])
    }

    SectionSpacer()
    ActionThemeModeSelector(themeMode = state.themeMode) {
        vm.setThemeMode(it)
    }
    SectionSpacer()
    SectionSpacer()
    SuggestedNavEntry()
}

@Composable
private fun ButtonListItem(
    index: Int,
    button: Button,
    evaluatingActions: List<String>,
    updateIcon: (Int, String) -> Unit,
    removeButton: (Int) -> Unit,
) {
    val iconLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val icon = result.popupSelectorResult<MDIcon>()
            icon?.let {
                updateIcon(index, it.iconNameOrBase64)
            }
        }
    )

    ShortXCard(modifier = Modifier.padding(top = 16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = SpaceBetween) {
                MediumTitle(
                    text = LocalI18N.current["ui.action.show.overlay.button.of", mapOf(
                        "index" to "${index + 1}"
                    )]
                )
                IconButton(onClick = {
                    removeButton(index)
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            }

            StandardSpacer()

            Column(verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current["ui.action.show.overlay.icon"]
                    )
                    IconButton(onClick = {
                        updateIcon(index, "")
                    }) {
                        RemixIcon(
                            modifier = Modifier.size(18.dp),
                            remixName = Remix.Device.restart_line
                        )
                    }
                }
                val context = LocalContext.current
                if (button.icon.isBlank()) {
                    TextButton(onClick = {
                        iconLauncher.launch(
                            BaseSelectorActivity.newIntent(
                                context,
                                null,
                                MDIconSelectorActivity::class.java
                            )
                        )
                    }) {
                        Text(text = "No icon")
                    }
                } else {
                    IconButton(onClick = {
                        iconLauncher.launch(
                            BaseSelectorActivity.newIntent(
                                context,
                                null,
                                MDIconSelectorActivity::class.java
                            )
                        )
                    }) {
                        RemixIcon(button.icon)
                    }
                }
            }

            val actionStateHandle = remember(button) { button.actionHandle }
            val actionState by actionStateHandle.state.collectAsState()

            ActionContent(
                header = {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.actions.single.click.title"]
                    )
                },
                actions = actionState.actions,
                evaluatingActions = evaluatingActions,
                warnings = actionState.actionWarningMessages,
                handler = actionStateHandle.asActionHandler()
            )

            StandardSpacer()
            val longClickActionStateHandle = remember(button) { button.longClickActionHandle }
            val longClickActionState by longClickActionStateHandle.state.collectAsState()

            ActionContent(
                header = {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.actions.long.click.title"]
                    )
                },
                actions = longClickActionState.actions,
                evaluatingActions = evaluatingActions,
                warnings = longClickActionState.actionWarningMessages,
                handler = longClickActionStateHandle.asActionHandler()
            )
        }
    }
}
