@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalSnackbarHostState
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.main.MainViewModel
import tornaco.apps.shortx.ui.main.da.DAItemHandler
import tornaco.apps.shortx.ui.main.da.DirectActionListContent
import tornaco.apps.shortx.ui.main.da.LocalDAItemHandler
import tornaco.apps.shortx.ui.main.model.DirectActionUM
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
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.title.select.action"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                        },
                    )
                }) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    val daItemHandler = object : DAItemHandler {
                        override fun click(da: DirectActionUM) {
                            dialogState.onSelected(da)
                            dialogState.dismiss()
                        }
                    }
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides SnackbarHostState(),
                        LocalDAItemHandler provides daItemHandler
                    ) {
                        DirectActionListContent(
                            isLoading = mainState.isLoading,
                            isShowDetailedDAItem = mainState.isShowDetailedDAItem,
                            das = mainState.das,
                            showOverflowMenu = false,
                        )
                    }
                }

            }
        }
    }

}