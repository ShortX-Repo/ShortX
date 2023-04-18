@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.main.DirectActionListContent
import tornaco.apps.shortx.ui.main.DirectActionUM
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.theme.ShortXTheme


class DADialogState(
    val onSelected: (DirectActionUM) -> Unit
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
fun rememberDADialogState(
    onSelected: (DirectActionUM) -> Unit
): DADialogState {
    return remember {
        DADialogState(onSelected)
    }
}

@Composable
fun DASelectorDialog(state: DADialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DASelector(state)
        }
    }
}

@Composable
private fun DASelector(dialogState: DADialogState) {
    val mainViewModel = hiltViewModel<MainViewModel>().apply { resetState() }
    val mainState by mainViewModel.state.collectAsState()

    LaunchedEffect(mainViewModel) {
        mainViewModel.loadDA()
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.title.select.action"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.Arrows.arrow_left_line)
                            }
                        },
                        actions = {
                        },
                    )
                }) {

                DirectActionListContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    state = mainState,
                    showOverflowMenu = false,
                    onDirectActionClick = { da ->
                        dialogState.onSelected(da)
                        dialogState.dismiss()
                    }
                )
            }
        }
    }

}