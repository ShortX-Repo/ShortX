@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.pkgset.PkgSetSelectorDialog
import tornaco.apps.shortx.ui.pkgset.rememberPkgSetSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme

class PkgSetAppMutationSelectorDialogState(
    val title: String,
    val onSelected: (String, List<StringPair>) -> Unit
) : CommonDialogState() {
    var pkgSetLabel by mutableStateOf("")
    var selectedPairs by mutableStateOf(emptyList<StringPair>())

    fun show(currentPkgSetLabel: String? = null, currentPairs: List<StringPair>? = null) {
        pkgSetLabel = currentPkgSetLabel.orEmpty()
        selectedPairs = currentPairs ?: emptyList()
        show()
    }

    fun addItem(item: StringPair) {
        selectedPairs = selectedPairs.toMutableList().apply {
            add(item)
        }
    }

    fun remove(index: Int) {
        selectedPairs = selectedPairs.toMutableList().apply {
            removeAt(index)
        }
    }
}

@Composable
fun rememberPkgSetAppMutationSelectorDialogState(
    title: String,
    onSelected: (String, List<StringPair>) -> Unit
): PkgSetAppMutationSelectorDialogState {
    return remember {
        PkgSetAppMutationSelectorDialogState(title, onSelected)
    }
}

@Composable
fun PkgSetAppMutationSelectorDialog(state: PkgSetAppMutationSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            PkgSetAppMutationDialogContent(state)
        }
    }
}

@Composable
private fun PkgSetAppMutationDialogContent(state: PkgSetAppMutationSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    ShortXTopAppBar(
                        title = { Text(state.title) },
                        navigationIcon = {
                            IconButton(onClick = { state.dismiss() }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (state.pkgSetLabel.isNotBlank() && state.selectedPairs.isNotEmpty()) {
                                    state.onSelected(state.pkgSetLabel, state.selectedPairs)
                                    state.dismiss()
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                val i18N = LocalI18N.current
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    val pkgSetSelectorState = rememberPkgSetSelectorState {
                        state.pkgSetLabel = it.label
                    }
                    PkgSetSelectorDialog(state = pkgSetSelectorState)

                    FilledTonalButton(onClick = { pkgSetSelectorState.show() }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(state.pkgSetLabel.ifBlank { i18N["ui.common.unselected"] })
                            StandardSpacer()
                            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                        }
                    }

                    StandardSpacer()

                    state.selectedPairs.forEachIndexed { index, pair ->
                        ListItem(
                            titleLines = listOf(pair.first, pair.second),
                            icon = {
                                RemixIcon(remixName = Remix.Logos.flickr_line)
                            },
                            action1 = {
                                IconButton(onClick = { state.remove(index) }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            onClick = {}
                        )
                    }

                    val singlePkgAndUserSelectState = rememberSinglePkgAndUserSelectorDialogState(
                        title = state.title,
                        onSelected = { state.addItem(it) }
                    )
                    SinglePkgAndUserSelectorDialog(state = singlePkgAndUserSelectState)

                    Button(onClick = { singlePkgAndUserSelectState.show() }) {
                        Text(text = i18N["ui.common.add"])
                    }
                }
            }
        }
    }
}
