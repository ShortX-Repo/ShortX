@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.gvar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme

class GlobalVarSelectorState(
    val onGlobalVarSelected: (GlobalVar) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberGlobalVarSelectorState(
    onGlobalVarSelected: (GlobalVar) -> Unit
): GlobalVarSelectorState {
    return remember {
        GlobalVarSelectorState(onGlobalVarSelected)
    }
}

@Composable
fun GlobalVarSelectorDialog(state: GlobalVarSelectorState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            GlobalVarSelector(state)
        }
    }
}

@Composable
private fun GlobalVarSelector(selectorState: GlobalVarSelectorState) {
    val vm = hiltViewModel<GlobalVarListViewModel>().apply { resetState() }
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadState()
    }
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.global.var.title"]) },
                        actions = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                selectorState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        }
                    )
                }) { paddingValues ->
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = StaggeredGridCells.Fixed(3),
                    contentPadding = paddingValues
                ) {
                    items(state.globalVars) {
                        GlobalVarListItem(modifier = Modifier,
                            name = it.name,
                            value = it.valueString(),
                            type = it.type.toType()?.label() ?: "?",
                            onClick = {
                                selectorState.onGlobalVarSelected(it)
                                selectorState.dismiss()
                            })
                    }
                }

            }
        }
    }
}


@Composable
private fun GlobalVarListItem(
    modifier: Modifier,
    name: String,
    value: String? = null,
    type: String,
    onClick: () -> Unit,
) {
    ShortXCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp), onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlineBadge(text = type)
            TinySpacer()
            Text(
                text = name, style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp,
                )
            )
        }
    }
}
