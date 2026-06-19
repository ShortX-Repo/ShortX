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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class PostNotificationSelectorActivity : BaseSelectorActivity<Action.PostNotification>() {
    @Composable
    override fun BaseSelectorActivity<Action.PostNotification>.SelectorContent(currentData: Action.PostNotification?) {
        PostNotificationSelector(currentData)
    }
}

@Composable
private fun BaseSelectorActivity<Action.PostNotification>.PostNotificationSelector(currentAction: Action.PostNotification?) {
    val vm = hiltViewModel<PostNotificationSelectorViewModel>()
    LaunchedEffect(vm) {
        if (currentAction == null) {
            vm.resetState()
        } else {
            vm.initFrom(currentAction)
        }
    }

    val state by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION]) },
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
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, vm: PostNotificationSelectorViewModel) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            FieldTitle(
                modifier = Modifier,
                LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_TAG]
            )
            TipBody(LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_TAG_DESCRIPTION])
            MediumSpacer()
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.tag,
                onValueChange = {
                    vm.updateTag(it)
                })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            FieldTitle(
                modifier = Modifier,
                LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_TITLE]
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
                LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_MESSAGE]
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
                LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_OVERRIDE_APP_NAME]
            )
            SmallSpacer()
            TipRow(text = LocalI18N.current[I18N_R.UI_COMMON_FEATURE_NOT_WORK_ON_3RD_ROM])
            MediumSpacer()
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.overrideAppName,
                onValueChange = {
                    vm.updateOverrideAppName(it)
                })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            FieldTitle(
                modifier = Modifier,
                LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_SMALL_ICON]
            )
            MediumSpacer()

            val smallIconLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    val icon = result.popupSelectorResult<MDIcon>()
                    icon?.let {
                        vm.updateSmallIcon(it.iconNameOrBase64)
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
                RemixIcon(state.smallIcon)
            }
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_LARGE_ICON])

            MediumSpacer()

            val largeIconLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    val icon = result.popupSelectorResult<MDIcon>()
                    icon?.let {
                        vm.updateLargeIcon(it.iconNameOrBase64)
                    }
                }
            )
            val context = LocalContext.current
            TextButton(onClick = {
                largeIconLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        null,
                        MDIconSelectorActivity::class.java
                    )
                )
            }) {
                if (state.largeIcon.isNotEmpty()) {
                    RemixIcon(state.largeIcon)
                }
            }
        }

        SectionSpacer()
        SwitchRow(
            text = LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_IMPORTANT],
            isChecked = state.isImportant,
            onCheckChange = {
                vm.setImportant(it)
            })

        SectionSpacer()
        SwitchRow(
            text = LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_SOUND],
            isChecked = state.sound,
            onCheckChange = {
                vm.setSound(it)
            })

        SectionSpacer()
        SwitchRow(
            text = LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_VIBRATE],
            isChecked = state.vibrate,
            onCheckChange = {
                vm.setVibrate(it)
            })
        SectionSpacer()
        SwitchRow(
            text = LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_ONGOING],
            isChecked = state.ongoing,
            onCheckChange = {
                vm.setOngoing(it)
            })


        SectionSpacer()
        FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_ON_CLICK])
        val onClickHandler = remember(state) { state.onClickHandler }
        val handlerState by onClickHandler.state.collectAsState()
        tornaco.apps.shortx.ui.addrule.action.ActionContent(
            header = {
                SmallTitle(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_ON_CLICK]
                )
            },
            actions = handlerState.actions,
            evaluatingActions = state.evaluatingActions,
            warnings = emptyMap(),
            handler = onClickHandler.asActionHandler("PostN onClick - ${state.onClickHandler}")
        )

        SectionSpacer()
        FieldTitle(text = LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_BUTTON])
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
                Text(text = LocalI18N.current[I18N_R.UI_ACTION_SHOW_ADD_BUTTON])
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
                    text = LocalI18N.current[I18N_R.UI_ACTION_SHOW_OVERLAY_BUTTON_OF, mapOf(
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
                    LocalI18N.current[I18N_R.UI_ACTION_POST_NOTIFICATION_BUTTON_TEXT]
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
                        text = LocalI18N.current[I18N_R.UI_ACTIONS_SINGLE_CLICK_TITLE]
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
