@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.UserInfo
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.core.i18n.I18N_R


class UserDialogState(
    val onSelected: (UserInfo) -> Unit
) {
    var isShowing by mutableStateOf(false)

    fun show() {
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberUserSelectorDialogState(
    onSelected: (UserInfo) -> Unit
): UserDialogState {
    return remember {
        UserDialogState(onSelected)
    }
}

@Composable
fun UserSelectorDialog(state: UserDialogState) {
    if (state.isShowing) {
        ShortXDialog(
            title = {
                DialogTitle(LocalI18N.current[I18N_R.UI_COMMON_USER_ID])
            },
            onDismissRequest = { state.dismiss() }) {
            UserSelector(state)
        }
    }
}

@Composable
private fun UserSelector(dialogState: UserDialogState) {
    val vm = hiltViewModel<UserSelectorViewModel>()
    LaunchedEffect(vm) { vm.loadUsers() }
    val state by vm.state.collectAsState()
    UserSelector(state, dialogState)
}

@Composable
private fun UserSelector(
    state: UserState,
    dialogState: UserDialogState,
) {
    Column(modifier = Modifier) {
        state.allUsers.forEach {
            ListItem(
                titleLines = listOf(it.name, it.userId.toString()),
                icon = {
                    RemixIcon(Remix.UserAndFaces.user_fill)
                },
                onClick = {
                    dialogState.onSelected(it)
                    dialogState.dismiss()
                })
        }
    }
}

@Composable
@Preview
fun UserSelectorPreview() {
    UserSelector(
        UserState(
            allUsers = listOf(
                UserInfo.newBuilder().setUserId(0).setIsMain(true).setName("TORNACO").build(),
                UserInfo.newBuilder().setUserId(10).setIsMain(false).setName("Nick").build(),
            )
        ),
        UserDialogState({})
    )
}
