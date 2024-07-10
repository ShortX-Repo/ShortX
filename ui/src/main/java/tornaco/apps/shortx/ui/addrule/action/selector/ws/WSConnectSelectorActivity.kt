package tornaco.apps.shortx.ui.addrule.action.selector.ws

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.CustomContextDataKey
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.ContextDataIcon
import tornaco.apps.shortx.ui.addrule.ContextDataInfoDialog
import tornaco.apps.shortx.ui.addrule.action.asActionHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.dats
import tornaco.apps.shortx.ui.addrule.rememberContextDataInfoDialogState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry


@AndroidEntryPoint
class WSConnectSelectorActivity : BaseSelectorActivity<Action.WebSocketConnect>() {
    @Composable
    override fun BaseSelectorActivity<Action.WebSocketConnect>.SelectorContent(currentData: Action.WebSocketConnect?) {
        WSSelector(currentData)
    }
}

@Composable
private fun BaseSelectorActivity<Action.WebSocketConnect>.WSSelector(currentAction: Action.WebSocketConnect?) {
    val vm = hiltViewModel<WSConnectSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.init(currentAction)
    }

    val dialogState by vm.state.collectAsState()

    val openHandler = remember(vm) { vm.openActionHandler }
    val openState by openHandler.state.collectAsState()

    val closeHandler = remember(vm) { vm.closeActionHandler }
    val closeState by closeHandler.state.collectAsState()

    val messageHandler = remember(vm) { vm.messageActionHandler }
    val messageState by messageHandler.state.collectAsState()

    val failureHandler = remember(vm) { vm.failureActionHandler }
    val failureState by failureHandler.state.collectAsState()

    val contextDataInfoDialogState = rememberContextDataInfoDialogState()
    ContextDataInfoDialog(state = contextDataInfoDialogState)

    ShortXAppBarScaffold(
        title = LocalI18N.current["WebSocket"],
        onBackPressed = {
            cancelAndFinish()
        },
        actions = {
            IconButton(onClick = {
                contextDataInfoDialogState.show(
                    objName = "WebSocket",
                    contextData = dats<ContextDataMapping.WebSocket>(
                        CustomContextDataKey.getDefaultInstance()
                    ),
                    canSetCustomKey = false,
                )
            }) {
                ContextDataIcon()
            }

            IconButton(onClick = {
                setResultAndFinish(
                    Action.WebSocketConnect(
                        id = dialogState.id,
                        url = dialogState.url,
                        openActions = openState.actions,
                        closeActions = closeState.actions,
                        messageActions = messageState.actions,
                        failureActions = failureState.actions,
                    )
                )
            }) {
                RemixIcon(remixName = Remix.System.check_line)
            }

        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FieldTitle(text = "Url")
            SmallSpacer()
            ShortXTextField(value = dialogState.url, onValueChange = {
                vm.inputUrl(it)
            })
            Spacer(modifier = Modifier.size(24.dp))

            tornaco.apps.shortx.ui.addrule.action.ActionContent(
                header = {
                    MediumTitle(LocalI18N.current["On Open"])
                },
                actions = openState.actions,
                evaluatingActions = dialogState.evaluatingActions,
                handler = openHandler.asActionHandler(),
                warnings = openState.actionWarningMessages
            )
            Spacer(modifier = Modifier.size(24.dp))

            tornaco.apps.shortx.ui.addrule.action.ActionContent(
                header = {
                    MediumTitle(LocalI18N.current["On Message"])
                },
                actions = messageState.actions,
                evaluatingActions = dialogState.evaluatingActions,
                handler = messageHandler.asActionHandler(),
                warnings = messageState.actionWarningMessages
            )
            Spacer(modifier = Modifier.size(24.dp))

            tornaco.apps.shortx.ui.addrule.action.ActionContent(
                header = {
                    MediumTitle(LocalI18N.current["On Close"])
                },
                actions = closeState.actions,
                evaluatingActions = dialogState.evaluatingActions,
                handler = closeHandler.asActionHandler(),
                warnings = closeState.actionWarningMessages
            )
            Spacer(modifier = Modifier.size(24.dp))

            tornaco.apps.shortx.ui.addrule.action.ActionContent(
                header = {
                    MediumTitle(LocalI18N.current["On Failure"])
                },
                actions = failureState.actions,
                evaluatingActions = dialogState.evaluatingActions,
                handler = failureHandler.asActionHandler(),
                warnings = failureState.actionWarningMessages
            )
            Spacer(modifier = Modifier.size(24.dp))


            SuggestedNavEntry()
        }
    }
}
