@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.HttpRequestHeader
import tornaco.apps.shortx.core.proto.common.HttpRequestMethod
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.HttpRequestAdapter
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitle
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.MenuDialogState
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
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
    internal var method by mutableStateOf(HttpRequestMethod.GET)
    internal var headers by mutableStateOf<List<HttpRequestHeader>>(emptyList())

    internal var expressionInput by mutableStateOf("")
    internal var expressions by mutableStateOf(listOf<String>())
    internal var output by mutableStateOf("")
    internal var adapter by mutableStateOf(AdapterType.Raw)

    fun show(currentAction: Action.HttpRequest? = null) {
        this.url = currentAction?.url ?: ""
        this.method = currentAction?.method ?: HttpRequestMethod.GET
        this.headers = currentAction?.headers ?: emptyList()

        this.output = ""
        this.expressionInput = ""
        this.expressions = currentAction?.let {
            if (it.adapter is HttpRequestAdapter.JsonMap) {
                it.adapter.expressions
            } else {
                emptyList()
            }
        } ?: emptyList()
        this.adapter = currentAction?.let {
            if (it.adapter is HttpRequestAdapter.JsonMap) AdapterType.Json else AdapterType.Raw
        } ?: AdapterType.Raw

        show()
    }

    fun setMethod(method: HttpRequestMethod) {
        this.method = method
    }

    fun addHeader(header: HttpRequestHeader) {
        this.headers = this.headers.toMutableList().apply {
            add(header)
        }
    }

    fun removeHeader(index: Int) {
        this.headers = this.headers.toMutableList().apply {
            removeAt(index)
        }
    }

    fun addExpression(expression: String) {
        this.expressions = this.expressions.toMutableList().apply {
            add(expression)
        }
    }

    fun removeExpression(expression: Int) {
        this.expressions = this.expressions.toMutableList().apply {
            removeAt(expression)
        }
    }

    fun updateExpression(expression: String, index: Int) {
        this.expressions = this.expressions.toMutableList().apply {
            set(index, expression)
        }
    }

    fun inputExpression(expression: String) {
        this.expressionInput = expression
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
                                            method = dialogState.method,
                                            headers = dialogState.headers,
                                            adapter = if (dialogState.adapter == AdapterType.Json) {
                                                HttpRequestAdapter.JsonMap(expressions = dialogState.expressions)
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

                    StandardSpacer()
                    FieldTitle(text = "Method")
                    val methodDropdownState = rememberDropdownSelectorState()
                    DropdownSelector(
                        state = methodDropdownState,
                        items = HttpRequestMethod.values()
                            .filterNot { it == HttpRequestMethod.UNRECOGNIZED }.map {
                                DropdownItem(
                                    labelLines = listOf(it.name),
                                    data = it
                                )
                            },
                        onSelect = {
                            dialogState.setMethod(it.data)
                        })

                    StandardSpacer()
                    FieldTitle(text = "Headers")
                    dialogState.headers.forEach {
                        ListItem(titleLines = listOf(it.key, it.value))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var keyInput by remember {
                            mutableStateOf("")
                        }
                        var valueInput by remember {
                            mutableStateOf("")
                        }
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = keyInput,
                            onValueChange = {
                                keyInput = it
                            },
                            label = {
                                Text(text = "Key")
                            },
                            showSymbolButton = false
                        )
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = valueInput,
                            onValueChange = {
                                valueInput = it
                            },
                            label = {
                                Text(text = "Value")
                            },
                            trailingIcon = {
                                FilledTonalIconButton(
                                    onClick = {
                                        if (keyInput.isNotEmpty() && valueInput.isNotEmpty()) {
                                            dialogState.addHeader(
                                                HttpRequestHeader.newBuilder()
                                                    .setKey(keyInput)
                                                    .setValue(valueInput)
                                                    .build()
                                            )
                                        }
                                    }) {
                                    RemixIcon(remixName = Remix.System.add_fill)
                                }
                            },
                            showSymbolButton = false
                        )
                    }

                    StandardSpacer()
                    Column(modifier = Modifier) {
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

                    AnimatedVisibility(visible = dialogState.adapter == AdapterType.Raw) {
                        val contextDataKey =
                            "{${ContextDataMapping.HttpRequest.httpRequestRet.name}}"
                        ListItem(
                            title = LocalI18N.current["ui.action.http.request.json.expression.key", mapOf(
                                "key" to contextDataKey
                            )],
                            onClick = {
                                ClipboardUtils.copyToClipboard(
                                    context,
                                    "key",
                                    contextDataKey
                                )
                                Toast.makeText(
                                    context,
                                    i18N["ui.common.copied.to.clipboard"],
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }

                    AnimatedVisibility(visible = dialogState.adapter == AdapterType.Json) {
                        Column {
                            TipRow(text = LocalI18N.current["ui.action.http.request.json.expression.description"])
                            StandardSpacer()
                            dialogState.expressions.forEachIndexed { index, s ->
                                ExpressionItem(expression = s, index = index) {
                                    dialogState.removeExpression(index)
                                }
                            }
                            StandardSpacer()
                            ShortXTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = dialogState.expressionInput,
                                onValueChange = {
                                    dialogState.inputExpression(it)
                                },
                                label = {
                                    Text(text = LocalI18N.current["ui.action.http.request.json.expression"])
                                },
                                trailingIcon = {
                                    FilledTonalIconButton(
                                        onClick = {
                                            if (dialogState.expressionInput.isNotBlank()) {
                                                dialogState.addExpression(dialogState.expressionInput)
                                            }
                                        }) {
                                        RemixIcon(remixName = Remix.System.add_fill)
                                    }
                                },
                                showSymbolButton = false
                            )

                        }
                    }
                }
            }
        }
    }

}


@Composable
private fun ExpressionItem(
    expression: String,
    index: Int,
    remove: () -> Unit,
) {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    val contextDataKey = "{${ContextDataMapping.HttpRequest.httpRequestRet.name}${index + 1}}"
    ShortXCard(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp),
        onClick = {
            ClipboardUtils.copyToClipboard(
                context,
                "key",
                contextDataKey
            )
            Toast.makeText(
                context,
                i18N["ui.common.copied.to.clipboard"],
                Toast.LENGTH_LONG
            ).show()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .heightIn(min = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = expression,
                    fontSize = 14.5.sp
                )
                SmallSpacer()
                Text(
                    text = LocalI18N.current["ui.action.http.request.json.expression.key", mapOf("key" to contextDataKey)],
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = {
                remove()
            }) {
                RemixIcon(remixName = Remix.System.close_line)
            }
        }
    }
}