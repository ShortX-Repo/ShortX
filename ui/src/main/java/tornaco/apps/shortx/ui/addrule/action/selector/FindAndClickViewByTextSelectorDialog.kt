@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXSwitch
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.theme.ShortXTheme

class FindAndClickViewByTextSelectorState(
    val onSelected: (Action.FindAndClickViewByText) -> Unit
) : CommonDialogState() {
    private var _text by mutableStateOf("")
    val text get() = _text

    private var _isRegex by mutableStateOf(false)
    val isRegex get() = _isRegex

    private var _timeout by mutableStateOf(3000L)
    val timeout get() = _timeout

    fun show(current: Action.FindAndClickViewByText? = null) {
        this._text = current?.text ?: ""
        this._isRegex = current?.isRegex ?: false
        this._timeout = current?.timeout ?: 3000L

        show()
    }


    fun setText(text: String) {
        _text = text
    }

    fun setIsRegex(isRegex: Boolean) {
        _isRegex = isRegex
    }

    fun setTimeout(timeout: Long) {
        _timeout = timeout
    }
}

@Composable
fun rememberFindAndClickViewByTextSelectorState(
    onSelected: (Action.FindAndClickViewByText) -> Unit
): FindAndClickViewByTextSelectorState {
    return remember {
        FindAndClickViewByTextSelectorState(onSelected)
    }
}

@Composable
fun FindAndClickViewByTextSelectorDialog(state: FindAndClickViewByTextSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            FindAndClickViewByTextSelector(state)
        }
    }
}

@Composable
private fun FindAndClickViewByTextSelector(
    state: FindAndClickViewByTextSelectorState
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(text = LocalI18N.current["ui.action.find.click.view.by.text"]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            state.dismiss()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (state.text.isNotBlank()) {
                                state.onSelected(
                                    Action.FindAndClickViewByText(
                                        text = state.text,
                                        isRegex = state.isRegex,
                                        timeout = state.timeout
                                    )
                                )
                                state.dismiss()
                            }
                        }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                        }
                    },
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                ) {
                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current["ui.action.find.click.view.by.text.text"]
                    )
                    TinySpacer()
                    ShortXTextField(modifier = Modifier.fillMaxWidth(),
                        value = state.text,
                        onValueChange = {
                            state.setText(it)
                        })
                    StandardSpacer()

                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current["ui.action.find.click.view.by.text.is.regex"]
                    )
                    TinySpacer()
                    ShortXSwitch(
                        checked = state.isRegex,
                        onCheckedChange = { state.setIsRegex(it) })
                    StandardSpacer()

                    SmallTitle(
                        modifier = Modifier.padding(vertical = 12.dp),
                        LocalI18N.current["ui.action.find.click.view.by.text.timeout"]
                    )
                    TipBody(LocalI18N.current["ui.action.find.click.view.by.text.timeout.description"])
                    TinySpacer()
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.timeout.toString(),
                        onValueChange = {
                            if (it.isDigitsOnly()) {
                                state.setTimeout(kotlin.runCatching { it.toLong() }
                                    .getOrElse { 0 })
                            }
                        },
                        showSymbolButton = false,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    StandardSpacer()
                }
            }
        }
    }
}