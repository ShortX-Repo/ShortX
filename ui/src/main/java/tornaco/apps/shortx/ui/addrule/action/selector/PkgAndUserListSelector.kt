@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import tornaco.apps.shortx.ui.theme.ShortXTheme

class PkgAndUserListSelectorDialogState(
    val title: String,
    val onSelected: (List<StringPair>) -> Unit
) : CommonDialogState() {
    var selectedPairs by mutableStateOf(emptyList<StringPair>())

    fun show(current: List<StringPair>? = null) {
        selectedPairs = current ?: emptyList()
        show()
    }

    fun addItem(item: StringPair) {
        selectedPairs = selectedPairs.toMutableList().apply {
            add(item)
        }
    }

    fun remove(item: Int) {
        selectedPairs = selectedPairs.toMutableList().apply {
            removeAt(item)
        }
    }
}

@Composable
fun rememberPkgAndUserListSelectorDialogState(
    title: String,
    onSelected: (List<StringPair>) -> Unit
): PkgAndUserListSelectorDialogState {
    return remember {
        PkgAndUserListSelectorDialogState(title, onSelected)
    }
}

@Composable
fun PackageNameAndUserSelectorDialog(state: PkgAndUserListSelectorDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DialogContent(state)
        }
    }
}

@Composable
private fun DialogContent(dialogState: PkgAndUserListSelectorDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(dialogState.title) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (dialogState.selectedPairs.isNotEmpty()) {
                                    dialogState.onSelected(
                                        dialogState.selectedPairs
                                    )
                                    dialogState.dismiss()
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    dialogState.selectedPairs.forEachIndexed { index, it ->
                        ListItem(
                            titleLines = listOf(it.first, it.second),
                            icon = {
                                RemixIcon(remixName = Remix.Logos.flickr_line)
                            },
                            action1 = {
                                IconButton(onClick = {
                                    dialogState.remove(index)
                                }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            onClick = {
                            })
                    }

                    val singlePkgAndUserSelectState = rememberSinglePkgAndUserSelectorDialogState(
                        title = dialogState.title,
                        onSelected = {
                            dialogState.addItem(it)
                        }
                    )
                    SinglePkgAndUserSelectorDialog(state = singlePkgAndUserSelectState)

                    androidx.compose.material3.Button(onClick = {
                        singlePkgAndUserSelectState.show()
                    }) {
                        Text(text = LocalI18N.current["ui.common.add"])
                    }
                    StandardSpacer()
                }
            }
        }
    }

}
