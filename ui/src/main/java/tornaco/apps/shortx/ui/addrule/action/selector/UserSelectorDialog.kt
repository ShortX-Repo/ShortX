@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.UserInfo
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.*


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
                DialogTitle(LocalI18N.current["ui.common.user.id"])
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
