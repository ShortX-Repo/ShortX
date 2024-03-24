package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.protobuf.ByteString
import tornaco.apps.shortx.core.INFCTagEndpointListener
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.subscribe.LottieNFCFlashView

class NFCTagSelectorState(val onSelected: (ByteString) -> Unit) : CommonDialogState()

@Composable
fun rememberNFCTagSelectorState(
    selected: (ByteString) -> Unit
): NFCTagSelectorState {
    return remember {
        NFCTagSelectorState(selected)
    }
}

@Composable
fun NFCTagSelectorDialog(state: NFCTagSelectorState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.fact.nfc.tag"])
        }, buttons = {
            TextButton(onClick = {
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }, content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = LocalI18N.current["ui.fact.nfc.tag.record.tip"])
                StandardSpacer()
                LottieNFCFlashView()
                StandardSpacer()
                TipRow(text = LocalI18N.current["ui.fact.nfc.tag.scope.tip"])
            }

            val listener = remember {
                object : INFCTagEndpointListener.Stub() {
                    override fun onDispatchTagEndpoint(uid: ByteArray?) {
                        uid?.let {
                            state.onSelected(ByteString.copyFrom(it))
                            state.dismiss()
                        }
                    }
                }
            }
            LaunchedEffect(key1 = state) {
                if (state.isShowing) {
                    shortXManager.registerNFCTagEndpointListener(listener)
                    composeLogger.d("registerNFCTagEndpointListener- $listener")
                }
            }

            DisposableEffect(key1 = state) {
                onDispose {
                    shortXManager.unregisterNFCTagEndpointListener(listener)
                    composeLogger.d("unregisterNFCTagEndpointListener- $listener")
                }
            }
        })
    }
}