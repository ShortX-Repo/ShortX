@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.pkgset

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.theme.ShortXTheme

class PkgSetSelectorState(
    val onPkgSetSelected: (PkgSet) -> Unit
) : CommonDialogState() {
}

@Composable
fun rememberPkgSetSelectorState(
    onPkgSetSelected: (PkgSet) -> Unit
): PkgSetSelectorState {
    return remember {
        PkgSetSelectorState(onPkgSetSelected)
    }
}

@Composable
fun PkgSetSelectorDialog(state: PkgSetSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            PkgSetSelector(state)
        }
    }
}

@Composable
private fun PkgSetSelector(selectorState: PkgSetSelectorState) {
    val vm = hiltViewModel<PkgSetListViewModel>().apply { resetState() }
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
                        title = { Text(text = LocalI18N.current["ui.pkg.set.title"]) },
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
                    items(state.pkgSets) {
                        PkgSetListItem(modifier = Modifier,
                            name = it.label,
                            onClick = {
                                selectorState.onPkgSetSelected(it)
                                selectorState.dismiss()
                            })
                    }
                }

            }
        }
    }
}


@Composable
private fun PkgSetListItem(
    modifier: Modifier,
    name: String,
    onClick: () -> Unit,
) {
    ShortXCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name, style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
            )
        }
    }
}
