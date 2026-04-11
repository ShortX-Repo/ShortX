@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.function.FunctionItemUiModel
import tornaco.apps.shortx.ui.function.FunctionListViewModel
import tornaco.apps.shortx.ui.theme.ShortXTheme

class FunctionDialogState(
    val onSelected: (FunctionItemUiModel) -> Unit
) : CommonDialogState()

@Composable
fun rememberFunctionDialogState(
    onSelected: (FunctionItemUiModel) -> Unit
): FunctionDialogState {
    return remember {
        FunctionDialogState(onSelected)
    }
}

@Composable
fun FunctionSelectorDialog(state: FunctionDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            FunctionSelector(state)
        }
    }
}

@Composable
private fun FunctionSelector(dialogState: FunctionDialogState) {
    val vm = hiltViewModel<FunctionListViewModel>().apply { resetState() }
    val state by vm.state.collectAsState()
    val i18n = LocalI18N.current

    LaunchedEffect(vm) {
        vm.loadState()
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    ShortXTopAppBar(
                        title = { Text(i18n["ui.title.select.function"]) },
                        navigationIcon = {
                            IconButton(onClick = { dialogState.dismiss() }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (state.functions.isEmpty()) {
                        Text(
                            text = i18n["ui.function.empty"],
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.functions, key = { it.id }) { function ->
                                ListItem(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    titleLines = listOf(
                                        function.name,
                                        i18n["ui.function.summary", mapOf(
                                            "type" to function.returnType,
                                            "params" to function.parametersCount.toString(),
                                            "actions" to function.actionsCount.toString(),
                                        )]
                                    ),
                                    icon = {
                                        RemixIcon(Remix.System.function_fill)
                                    },
                                    onClick = {
                                        dialogState.onSelected(function)
                                        dialogState.dismiss()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}