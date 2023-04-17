@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


class ShowDanmuSettingsDialogState(
    val onSelected: (Action.ShowDanmu) -> Unit
) {
    var isShowing by mutableStateOf(false)
    var currentSettings: Action.ShowDanmu? = null

    fun show(currentSettings: Action.ShowDanmu? = null) {
        this.currentSettings = currentSettings
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberShowDanmuSettingsDialogState(
    onSelected: (Action.ShowDanmu) -> Unit
): ShowDanmuSettingsDialogState {
    return remember {
        ShowDanmuSettingsDialogState(onSelected)
    }
}

@Composable
fun ShowDanmuSettingsDialog(state: ShowDanmuSettingsDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShowDanmuSelector(state)
        }
    }
}

@Composable
private fun ShowDanmuSelector(dialogState: ShowDanmuSettingsDialogState) {
    val vm = hiltViewModel<ShowDanmuSelectorViewModel>().apply { resetState() }
    LaunchedEffect(vm) {
        dialogState.currentSettings?.let {
            vm.initFrom(it)
        }
    }
    val state: Action.ShowDanmu by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { LocalI18N.current["ui.action.show.dialog"] },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(state)
                                dialogState.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Done")
                            }

                        }
                    )
                }) {
                Content(paddingValues = it, vm = vm)
            }
        }
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, vm: ShowDanmuSelectorViewModel) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            shape = TextFieldDefaults.outlinedShape,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            label = {
                Text(text = LocalI18N.current["ui.action.danmu.text"])
            },
            value = state.text,
            onValueChange = {
                vm.updateText(it)
            })
        StandardSpacer()
        StandardSpacer()

        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            shape = TextFieldDefaults.outlinedShape,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            ),
            label = {
                Text(text = LocalI18N.current["ui.action.danmu.icon"])
            },
            value = state.icon ?: "",
            onValueChange = {
                vm.updateIcon(it)
            })
        StandardSpacer()
        StandardSpacer()
    }
}