@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody


class SinglePkgAndUserDialogState(
    val title: String,
    val onSelected: (StringPair) -> Unit
) : CommonDialogState() {
    var inputPackage by
    mutableStateOf("")
    var inputUser by
    mutableStateOf("$myUserId")

    override fun show() {
        this.inputPackage = ""
        this.inputUser = "$myUserId"
        super.show()
    }
}

@Composable
fun rememberSinglePkgAndUserSelectorDialogState(
    title: String,
    onSelected: (StringPair) -> Unit
): SinglePkgAndUserDialogState {
    return remember {
        SinglePkgAndUserDialogState(title, onSelected)
    }
}

@Composable
fun SinglePkgAndUserSelectorDialog(state: SinglePkgAndUserDialogState) {
    if (state.isShowing) {
        ShortXDialog(
            title = {
                DialogTitle(state.title)
            },
            buttons = {
                ShortXTextButton(
                    onClick = {
                        if (state.inputPackage.isNotEmpty()) {
                            state.onSelected(
                                StringPair.newBuilder().setFirst(state.inputPackage)
                                    .setSecond(state.inputUser).build()
                            )
                            state.dismiss()
                        }
                    }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            onDismissRequest = { state.dismiss() }) {
            LargeSpacer()
            FieldTitle(
                modifier = Modifier.padding(vertical = 12.dp),
                text = LocalI18N.current["ui.action.select.pkg.and.user.package"]
            )
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.inputPackage,
                onValueChange = {
                    state.inputPackage = it
                })
            StandardSpacer()

            val userSelectDialogState = rememberUserSelectorDialogState(onSelected = {
                state.inputUser = it.userId.toString()
            })
            UserSelectorDialog(state = userSelectDialogState)
            FieldTitle(
                modifier = Modifier.padding(vertical = 12.dp),
                text = LocalI18N.current["ui.action.select.pkg.and.user.user"]
            )
            TipBody(text = LocalI18N.current["ui.action.select.pkg.and.user.user.summary"])
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.inputUser,
                showSymbolButton = false,
                trailingIcon = {
                    IconButton(onClick = { userSelectDialogState.show() }) {
                        RemixIcon(remixName = Remix.UserAndFaces.user_3_line)
                    }
                },
                onValueChange = {
                    state.inputUser = it.trim()
                })
            StandardSpacer()
        }
    }
}