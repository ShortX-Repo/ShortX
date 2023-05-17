@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.labelLinesForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
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
    val vm = hiltViewModel<FromDADialogSelectorViewModel>().apply { resetState() }
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
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.from.da"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (state.data.daId.isNotBlank()) {
                                    dialogState.onSelected(state.data)
                                    dialogState.dismiss()
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->

                val daSelectorState = rememberDADialogState {
                    vm.selectDA(it.id)
                }
                DASelectorDialog(daSelectorState)

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

                        if (state.data.daId.isNotBlank()) {
                            ListItem(titleLines = labelLinesForAction(action = state.data),
                                icon = {
                                    ColoredIcon(size = 36.dp) {
                                        RemixIcon(remixName = imageVectorForAction(action = Action.FromDA::class))
                                    }
                                })
                        }
                        FilledTonalIconButton(onClick = {
                            daSelectorState.show()
                        }) {
                            RemixIcon(remixName = Remix.System.settings_2_line)
                        }
                    }
                }
            }
        }
    }

}