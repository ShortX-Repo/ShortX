@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.UserInfo
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
        ShortXDialog(onDismissRequest = { state.dismiss() }) {
            UserSelector(state)
        }
    }
}

@Composable
private fun UserSelector(dialogState: UserDialogState) {
    val vm = hiltViewModel<UserSelectorViewModel>()
    LaunchedEffect(vm) { vm.loadUsers() }
    val state by vm.state.collectAsState()

    state.allUsers.forEach {
        ListItem(titleLines = listOf(it.name, it.userId.toString()), onClick = {
            dialogState.onSelected(it)
        })
    }
}
