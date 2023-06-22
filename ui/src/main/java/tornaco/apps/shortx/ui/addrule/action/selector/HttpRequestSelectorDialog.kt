@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.HttpRequestAdapter
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.net.URL

internal enum class AdapterType {
    Raw, Json
}

class HttpRequestDialogState(
    val onSelected: (Action.HttpRequest) -> Unit
) : CommonDialogState() {
    internal var url by mutableStateOf("")
    internal var expression by mutableStateOf("")
    internal var output by mutableStateOf("")
    internal var adapter by mutableStateOf(AdapterType.Raw)

    fun show(currentAction: Action.HttpRequest? = null) {
        this.url = currentAction?.url ?: ""
        this.output = ""
        this.expression = currentAction?.let {
            if (it.adapter is HttpRequestAdapter.JsonMap) it.adapter.expression else null
        } ?: ""
        this.adapter = currentAction?.let {
            if (it.adapter is HttpRequestAdapter.JsonMap) AdapterType.Json else AdapterType.Raw
        } ?: AdapterType.Raw

        show()
    }

    fun updateExpression(expression: String) {
        this.expression = expression
    }

    fun setOutput(output: String) {
        this.output = output
    }
}

@Composable
fun rememberHttpRequestDialogState(
    onSelected: (Action.HttpRequest) -> Unit
): HttpRequestDialogState {
    return remember {
        HttpRequestDialogState(onSelected)
    }
}

@Composable
fun HttpRequestDialog(state: HttpRequestDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            HttpRequestSelector(state)
        }
    }
}

@Composable
private fun HttpRequestSelector(dialogState: HttpRequestDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.action.http.request"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                if (dialogState.url.isNotBlank()) {
                                    dialogState.onSelected(
                                        Action.HttpRequest(
                                            url = dialogState.url,
                                            adapter = if (dialogState.adapter == AdapterType.Json) {
                                                HttpRequestAdapter.JsonMap(expression = dialogState.expression)
                                            } else {
                                                HttpRequestAdapter.Raw
                                            }
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
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val scope = rememberCoroutineScope()
                    ShortXTextField(
                        value = dialogState.url,
                        onValueChange = {
                            dialogState.url = it
                        },
                        label = {
                            Text(text = "Url")
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        val output = kotlin.runCatching {
                                            URL(dialogState.url).readText()
                                        }.getOrElse {
                                            Log.getStackTraceString(it)
                                        }
                                        dialogState.setOutput(output)
                                    }
                                }

                            }) {
                                RemixIcon(remixName = Remix.Media.play_fill)
                            }
                        },
                        showSymbolButton = false
                    )
                    StandardSpacer()

                    val i18N = LocalI18N.current
                    val context = LocalContext.current
                    AnimatedVisibility(visible = dialogState.output.isNotBlank()) {
                        Column {
                            ShortXCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 72.dp, max = 300.dp),
                                onClick = {
                                    ClipboardUtils.copyToClipboard(
                                        context,
                                        "output",
                                        dialogState.output
                                    )
                                    Toast.makeText(
                                        context,
                                        i18N["ui.common.copied.to.clipboard"],
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(
                                            rememberScrollState()
                                        )
                                ) {
                                    Text(
                                        modifier = Modifier.padding(16.dp),
                                        text = dialogState.output,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            StandardSpacer()

                        }
                    }


                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        MediumTitle(
                            text = LocalI18N.current["ui.action.http.request.adapter"]
                        )
                        MediumSpacer()
                        val adapterSelectorState: MenuDialogState<String> =
                            rememberMenuDialogState(title = LocalI18N.current["ui.action.http.request.adapter"],
                                message = null,
                                menuItems = listOf(
                                    MenuDialogItem(
                                        id = AdapterType.Raw.name,
                                        title = LocalI18N.current["ui.action.http.request.raw"],
                                        summary = LocalI18N.current["ui.action.http.request.raw.summary"]
                                    ),
                                    MenuDialogItem(
                                        id = AdapterType.Json.name,
                                        title = LocalI18N.current["ui.action.http.request.json"],
                                        summary = LocalI18N.current["ui.action.http.request.json.summary"],
                                    ),
                                ),
                                onItemSelected = { data, id ->
                                    dialogState.adapter = AdapterType.valueOf(id)
                                }
                            )
                        MenuDialog(state = adapterSelectorState)
                        FilledTonalButton(onClick = {
                            adapterSelectorState.show()
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = dialogState.adapter.let {
                                    if (it == AdapterType.Raw) {
                                        LocalI18N.current["ui.action.http.request.raw"]
                                    } else {
                                        LocalI18N.current["ui.action.http.request.json"]
                                    }
                                })
                                StandardSpacer()
                                RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                            }
                        }
                        LargeSpacer()
                    }

                    StandardSpacer()

                    AnimatedVisibility(visible = dialogState.adapter == AdapterType.Json) {
                        ShortXTextField(
                            value = dialogState.expression,
                            onValueChange = {
                                dialogState.updateExpression(it)
                            },
                            label = {
                                Text(text = LocalI18N.current["ui.action.http.request.json.expression"])
                            },
                            showSymbolButton = false
                        )
                    }
                }
            }
        }
    }

}