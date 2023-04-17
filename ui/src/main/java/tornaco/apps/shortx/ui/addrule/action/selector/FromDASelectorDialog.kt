@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme


class FromDADialogState(
    val onSelected: (Action.FromDA) -> Unit
) {
    var isShowing by mutableStateOf(false)

    var currentSettings: Action.FromDA? = null

    fun show(currentSettings: Action.FromDA? = null) {
        this.currentSettings = currentSettings
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberFromDADialogState(
    onSelected: (Action.FromDA) -> Unit
): FromDADialogState {
    return remember {
        FromDADialogState(onSelected)
    }
}

@Composable
fun FromDASelectorDialog(state: FromDADialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            FromDASelector(state)
        }
    }
}

@Composable
private fun FromDASelector(dialogState: FromDADialogState) {
    val vm = hiltViewModel<FromDADialogSelectorViewModel>()
    LaunchedEffect(dialogState.currentSettings) {
        dialogState.currentSettings?.let {
            vm.initFrom(it)
        }
    }

    val state by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.post.notification"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(state.data)
                                dialogState.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Done")
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
private fun Content(paddingValues: PaddingValues, vm: FromDADialogSelectorViewModel) {
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
            SmallTitle(LocalI18N.current["ui.title.select.action"])



            vm.selectDA(shortXManager.getAllDirectAction().first().id)
        }
    }
}