@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIcon
import tornaco.apps.shortx.ui.base.MDIconSelectorActivity
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry

@AndroidEntryPoint
class ShowPPNSelectorActivity : BaseSelectorActivity<Action.ShowPPN>() {
    @Composable
    override fun BaseSelectorActivity<Action.ShowPPN>.SelectorContent(currentData: Action.ShowPPN?) {
        ShowPPNSelector(currentData)
    }
}


@Composable
private fun BaseSelectorActivity<Action.ShowPPN>.ShowPPNSelector(currentAction: Action.ShowPPN?) {
    val vm = hiltViewModel<ShowPPNSelectorViewModel>()
    LaunchedEffect(vm) {
        if (currentAction == null) {
            vm.resetState()
        } else {
            vm.initFrom(currentAction)
        }
    }

    val state by vm.state.collectAsState()

    Scaffold(
        modifier = Modifier,
        topBar = {
            ShortXTopAppBar(
                modifier = Modifier,
                title = { Text(LocalI18N.current["ui.action.show.ppn"]) },
                navigationIcon = {
                    IconButton(onClick = {
                        cancelAndFinish()
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        setResultAndFinish(state.toAction())
                    }) {
                        RemixIcon(remixName = Remix.System.check_line)
                    }

                },
            )
        }) {
        Content(paddingValues = it, vm = vm)
    }
}

@Composable
private fun Content(paddingValues: PaddingValues, vm: ShowPPNSelectorViewModel) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {

        if (false) {
            StandardSpacer()
            Column(verticalArrangement = Arrangement.Center) {
                FieldTitle(
                    modifier = Modifier,
                    LocalI18N.current["ui.action.post.notification.tag"]
                )
                TipBody(LocalI18N.current["ui.action.post.notification.tag.description"])
                MediumSpacer()
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.tag,
                    onValueChange = {
                        vm.updateTag(it)
                    })
            }
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            FieldTitle(
                modifier = Modifier,
                LocalI18N.current["ui.action.post.notification.title"]
            )
            MediumSpacer()
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = {
                    vm.updateTitle(it)
                })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            FieldTitle(
                modifier = Modifier,
                LocalI18N.current["ui.action.post.notification.message"]
            )
            MediumSpacer()
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.message,
                onValueChange = {
                    vm.updateMessage(it)
                })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            FieldTitle(
                modifier = Modifier,
                LocalI18N.current["ui.action.post.notification.small.icon"]
            )
            MediumSpacer()

            val smallIconLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    val icon = result.popupSelectorResult<MDIcon>()
                    icon?.let {
                        vm.updateIcon(it.iconNameOrBase64)
                    }
                }
            )
            val context = LocalContext.current
            TextButton(onClick = {
                smallIconLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        null,
                        MDIconSelectorActivity::class.java
                    )
                )
            }) {
                RemixIcon(state.icon)
            }
        }

        SectionSpacer()
        SwitchRow(
            text = LocalI18N.current["ui.action.post.notification.sound"],
            isChecked = state.sound,
            onCheckChange = {
                vm.setSound(it)
            })

        SectionSpacer()
        SwitchRow(
            text = LocalI18N.current["ui.action.post.notification.vibrate"],
            isChecked = state.vibrate,
            onCheckChange = {
                vm.setVibrate(it)
            })

        SectionSpacer()
        FieldTitle(text = LocalI18N.current["ui.action.post.notification.on.click"])
        val onClickHandler = remember(state) { state.onClickHandler }
        val handlerState by onClickHandler.state.collectAsState()
        tornaco.apps.shortx.ui.addrule.action.ActionContent(
            header = {
                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = LocalI18N.current["ui.action.post.notification.on.click"]
                )
            },
            actions = handlerState.actions,
            evaluatingActions = state.evaluatingActions,
            warnings = emptyMap(),
            handler = onClickHandler.asActionHandler("PostN onClick - ${state.onClickHandler}")
        )

        SectionSpacer()
        FieldTitle(text = LocalI18N.current["ui.action.post.notification.button"])
        MediumSpacer()
        Column {
            state.buttonAndHandlers.forEachIndexed { index, button ->
                ButtonListItem(
                    index = index,
                    buttonAndHandler = button,
                    evaluatingActions = state.evaluatingActions,
                    removeButton = {
                        vm.removeButton(it)
                    },
                    updateLabel = { ix, s ->
                        vm.updateLabel(ix, s)
                    })
            }
            StandardSpacer()
            // Add icon
            Button(
                modifier = Modifier.fillMaxSize(),
                onClick = {
                    vm.newButton()
                }) {
                Text(text = LocalI18N.current["ui.action.show.add.button"])
            }
        }


        SuggestedNavEntry()
    }
}


@Composable
private fun ButtonListItem(
    index: Int,
    buttonAndHandler: NotificationButtonAndHandler,
    evaluatingActions: List<String>,
    removeButton: (Int) -> Unit,
    updateLabel: (Int, String) -> Unit,
) {
    ShortXCard(modifier = Modifier.padding(top = 16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    LocalI18N.current["ui.action.post.notification.button.text"]
                )
                ShortXTextField(value = buttonAndHandler.label, onValueChange = {
                    updateLabel(index, it)
                })
            }
            StandardSpacer()

            val actionState by buttonAndHandler.handler.state.collectAsState()

            tornaco.apps.shortx.ui.addrule.action.ActionContent(
                header = {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.actions.single.click.title"]
                    )
                },
                actions = actionState.actions,
                evaluatingActions = evaluatingActions,
                warnings = actionState.actionWarningMessages,
                handler = buttonAndHandler.handler.asActionHandler("N.SingleClick")
            )
        }
    }
}
