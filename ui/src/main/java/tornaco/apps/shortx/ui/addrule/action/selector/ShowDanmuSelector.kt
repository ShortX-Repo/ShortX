@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.BrowserUtils


class ShowDanmuSettingsDialogState(
    val onSelected: (Action.ShowDanmu) -> Unit
) {
    var isShowing by mutableStateOf(false)
    var currentSettings: Action.ShowDanmu? = null

    fun show(currentSettings: Action.ShowDanmu? = null) {
        this.currentSettings = currentSettings
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberShowDanmuSettingsDialogState(
    onSelected: (Action.ShowDanmu) -> Unit
): ShowDanmuSettingsDialogState {
    return remember {
        ShowDanmuSettingsDialogState(onSelected)
    }
}

@Composable
fun ShowDanmuSettingsDialog(state: ShowDanmuSettingsDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShowDanmuSelector(state)
        }
    }
}

@Composable
private fun ShowDanmuSelector(dialogState: ShowDanmuSettingsDialogState) {
    val vm = hiltViewModel<ShowDanmuSelectorViewModel>()
    LaunchedEffect(vm) {
        val settings = dialogState.currentSettings
        if (settings == null) {
            vm.resetState()
        } else {
            vm.initFrom(settings)
        }
    }
    val state: Action.ShowDanmu by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.action.show.danmu"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(state)
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        }
                    )
                }) {
                Content(paddingValues = it, vm = vm)
            }
        }
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, vm: ShowDanmuSelectorViewModel) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {
        ShortXTextField(modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = LocalI18N.current["ui.action.danmu.text"])
            },
            value = state.text,
            onValueChange = {
                vm.updateText(it)
            })
        StandardSpacer()
        StandardSpacer()

        val context = LocalContext.current
        TipRow(text = "e.g. \nhttps://xxxx/123.png\napp://com.app.pkg\nremix://icon name at https://remixicon.com/")
        SmallSpacer()
        ShortXTextField(modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = LocalI18N.current["ui.action.danmu.icon"])
            },
            value = state.icon ?: "",
            onValueChange = {
                vm.updateIcon(it)
            },
            trailingIcon = {
                IconButton(onClick = { BrowserUtils.launch(context, "https://remixicon.com/") }) {
                    RemixIcon(remixName = Remix.Logos.remixicon_fill)
                }
            })
        StandardSpacer()
        StandardSpacer()
    }
}