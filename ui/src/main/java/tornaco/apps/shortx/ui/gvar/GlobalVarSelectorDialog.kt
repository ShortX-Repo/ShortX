@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.gvar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.label
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.ui.base.*
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
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.global.var.title"]) },
                        actions = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                selectorState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.Arrows.arrow_left_line)
                            }
                        }
                    )
                }) { paddingValues ->
                LazyVerticalStaggeredGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = paddingValues
                ) {
                    items(state.globalVars) {
                        androidx.compose.material.Card(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            backgroundColor = ColorDefaults.backgroundSurfaceColor(),
                            shape = ShortXCardRoundedCornerShape,
                            elevation = 0.dp
                        ) {
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
}


@Composable
private fun GlobalVarListItem(
    modifier: Modifier,
    name: String,
    value: String? = null,
    type: String,
    onClick: () -> Unit,
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
            onClick()
        }) {

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name, style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                    )
                )
                MD3Badge(text = type)
            }

            MediumSpacer()
            Text(value ?: "", fontSize = 12.sp)
            StandardSpacer()
        }
    }
}
