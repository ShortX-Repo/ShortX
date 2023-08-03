@file:OptIn(
    ExperimentalMaterial3Api::class
)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme

class StopAppByPkgDialogState(
    val onSelected: (Action.StopAppByPkg) -> Unit
) : CommonDialogState() {
    var selectedPairs by mutableStateOf(emptyList<StringPair>())

    fun show(current: Action.StopAppByPkg? = null) {
        selectedPairs = current?.stringPairs ?: emptyList()
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
fun rememberStopAppByPkgDialogState(
    onSelected: (Action.StopAppByPkg) -> Unit
): StopAppByPkgDialogState {
    return remember {
        StopAppByPkgDialogState(onSelected)
    }
}

@Composable
fun StopAppByPkgDialog(state: StopAppByPkgDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            StartActivityIntentSelector(state)
        }
    }
}

@Composable
private fun StartActivityIntentSelector(dialogState: StopAppByPkgDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.stop.app.by.pkg"]) },
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
                                        Action.StopAppByPkg(
                                            stringPairs = dialogState.selectedPairs
                                        )
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
                    var inputPackage by remember {
                        mutableStateOf("")
                    }
                    var inputUser by remember {
                        mutableStateOf("")
                    }

                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.action.stop.app.by.pkg.package"]
                    )
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = inputPackage,
                        onValueChange = {
                            inputPackage = it
                        })
                    StandardSpacer()

                    FieldTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = LocalI18N.current["ui.common.user.id"]
                    )
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = inputUser,
                        onValueChange = {
                            inputUser = it
                        })
                    StandardSpacer()


                    androidx.compose.material3.Button(onClick = {
                        dialogState.addItem(
                            StringPair.newBuilder()
                                .setFirst(inputPackage)
                                .setSecond(inputUser)
                                .build()
                        )
                        inputPackage = ""
                        inputUser = ""
                    }) {
                        Text(text = LocalI18N.current["ui.common.add"])
                    }
                    StandardSpacer()
                }
            }
        }
    }

}
