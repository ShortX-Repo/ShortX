package tornaco.apps.shortx.ui.addrule.action.selector.ws

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class WSSendSelectorActivity : BaseSelectorActivity<Action.WebSocketSend>() {
    @Composable
    override fun BaseSelectorActivity<Action.WebSocketSend>.SelectorContent(currentData: Action.WebSocketSend?) {
        WSSendSelector(currentData)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<Action.WebSocketSend>.WSSendSelector(currentAction: Action.WebSocketSend?) {
    val vm = hiltViewModel<WSSendSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.init(currentAction)
    }

    val dialogState by vm.state.collectAsState()


    ShortXAppBarScaffold(
        title = LocalI18N.current["WebSocket send"],
        onBackPressed = {
            cancelAndFinish()
        },
        actions = {
            IconButton(onClick = {
                setResultAndFinish(
                    Action.WebSocketSend(
                        id = dialogState.id,
                        url = dialogState.url,
                        message = dialogState.message,
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
            FieldTitleWithTip(
                text = "Url",
                tipText = LocalI18N.current[I18N_R.UI_ACTION_WEBSOCKET_SEND_URL_TIP]
            )
            SmallSpacer()
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dialogState.url,
                onValueChange = {
                    vm.inputUrl(it)
                })
            LargeSpacer()

            FieldTitle(text = "Message")
            SmallSpacer()
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dialogState.message,
                onValueChange = {
                    vm.inputMessage(it)
                })
            LargeSpacer()
        }
    }
}