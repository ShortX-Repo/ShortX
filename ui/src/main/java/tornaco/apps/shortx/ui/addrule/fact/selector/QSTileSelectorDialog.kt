package tornaco.apps.shortx.ui.addrule.fact.selector


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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.QSTile
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.theme.ShortXTheme

class QSTileSelectorState(
    val title: String,
    val onSelected: (QSTile) -> Unit
) : CommonDialogState()

@Composable
fun rememberQSTileSelectorState(
    title: String,
    onSelected: (QSTile) -> Unit
): QSTileSelectorState {
    return remember {
        QSTileSelectorState(
            title,
            onSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QSTileSelectorDialog(state: QSTileSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = state.title) },
                                actions = {
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                }
                            )
                        }) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            val tileList = remember {
                                shortXManager.getAllQSTiles().tileList
                            }
                            tileList.forEach {
                                tornaco.apps.shortx.ui.base.ListItem(
                                    titleLines = listOf(it.label, it.tileSpec),
                                    icon = {
                                        RemixIcon(remixName = Remix.Design.collage_line)
                                    },
                                    onClick = {
                                        state.onSelected(it)
                                        state.dismiss()
                                    })
                            }

                        }
                    }
                }
            }
        }
    }
}
