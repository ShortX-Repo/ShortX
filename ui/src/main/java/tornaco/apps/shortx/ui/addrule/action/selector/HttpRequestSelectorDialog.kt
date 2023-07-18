@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyForm
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyFormItem
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyJson
import tornaco.apps.shortx.core.proto.common.HttpRequestHeader
import tornaco.apps.shortx.core.proto.common.HttpRequestMethod
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.HttpRequestAdapter
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.MenuDialogState
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.code.OutputDialog
import tornaco.apps.shortx.ui.base.code.rememberOutputDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils

internal enum class AdapterType {
    Raw, Json
}

enum class HttpRequestBodyType(val label: String) {
    Json("Json"),
    Form("Form"),
    None("None")
}


class HttpRequestDialogState(
    val onSelected: (Action.HttpRequest) -> Unit
) : CommonDialogState() {
    internal var url by mutableStateOf("")
    internal var method by mutableStateOf(HttpRequestMethod.GET)
    internal var headers by mutableStateOf<List<HttpRequestHeader>>(emptyList())

    internal var requestBody by mutableStateOf(HttpRequestBodyType.None)
    internal var requestBodyJson by mutableStateOf<String>(
        "{}"
    )
    internal var requestBodyForm by mutableStateOf<List<HttpRequestBodyFormItem>>(
        emptyList()
    )

    internal var expressionInput by mutableStateOf("")
    internal var expressions by mutableStateOf(listOf<String>())
    internal var adapter by mutableStateOf(AdapterType.Raw)

    val httpRequest
        get() = Action.HttpRequest(
            url = this.url,
            method = this.method,
            headers = this.headers,
            requestBody = if (this.requestBody == HttpRequestBodyType.Json) {
                HttpRequestBodyJson.newBuilder().setJson(requestBodyJson).build().pack_()
            } else if (this.requestBody == HttpRequestBodyType.Form) {
                HttpRequestBodyForm.newBuilder().addAllItems(this.requestBodyForm).build().pack_()
            } else {
                ProtoAny.getDefaultInstance()
            },
            adapter = if (this.adapter == AdapterType.Json) {
                HttpRequestAdapter.JsonMap(expressions = this.expressions)
            } else {
                HttpRequestAdapter.Raw
            }
        )

    fun show(currentAction: Action.HttpRequest? = null) {
        this.url = currentAction?.url ?: ""
        this.method = currentAction?.method ?: HttpRequestMethod.GET
        this.headers = currentAction?.headers ?: emptyList()
        currentAction?.requestBody?.let {
            if (it is_ HttpRequestBodyJson::class.java) {
                val bodyJson = it unpack_ HttpRequestBodyJson::class.java
                this.requestBody = HttpRequestBodyType.Json
                this.requestBodyJson = bodyJson.json
            } else if (it is_ HttpRequestBodyForm::class.java) {
                val bodyForm = it unpack_ HttpRequestBodyForm::class.java
                this.requestBody = HttpRequestBodyType.Form
                this.requestBodyForm = bodyForm.itemsList
            } else {
                this.requestBody = HttpRequestBodyType.None
                this.requestBodyForm = emptyList()
                this.requestBodyJson = "{}"
            }
        } ?: HttpRequestBodyType.None

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

    fun inputExpression(expression: String) {
        this.expressionInput = expression
    }

    fun setRequestBodyType(bodyType: HttpRequestBodyType) {
        this.requestBody = bodyType
        if (bodyType != HttpRequestBodyType.None && this.method == HttpRequestMethod.GET) {
            this.method = HttpRequestMethod.POST
        }
    }

    fun addForm(item: HttpRequestBodyFormItem) {
        this.requestBodyForm = this.requestBodyForm.toMutableList().apply {
            add(item)
        }
    }

    fun removeForm(index: Int) {
        this.requestBodyForm = this.requestBodyForm.toMutableList().apply {
            removeAt(index)
        }
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
    val outputDialogState = rememberOutputDialogState(title = "ui.action.http.request")
    OutputDialog(state = outputDialogState)


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
                            val scope = rememberCoroutineScope()
                            IconButton(onClick = {
                                if (dialogState.url.isNotBlank()) {
                                    val request = dialogState.httpRequest
                                    scope.launch {
                                        val out = withContext(Dispatchers.IO) {
                                            shortXManager.executeHttpRequest(request.toProtoAction() as HttpRequest)
                                        }
                                        outputDialogState.show(out.joinToString(System.lineSeparator()))
                                    }
                                }
                            }) {
                                RemixIcon(remixName = Remix.Media.play_line)
                            }

                            IconButton(onClick = {
                                if (dialogState.url.isNotBlank()) {
                                    dialogState.onSelected(dialogState.httpRequest)
                                    dialogState.dismiss()
                                }
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }

                        },
                    )
                }) { paddingValues ->

                ClickOutsideClearFocusBox {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Url(dialogState)
                        StandardSpacer()

                        Method(dialogState)
                        StandardSpacer()

                        Headers(dialogState)
                        StandardSpacer()

                        RequestBody(dialogState)
                        StandardSpacer()

                        ResponseAdapter(dialogState)

                        SectionSpacer()
                        SuggestedNavEntry()
                    }
                }
            }
        }
    }

}

@Composable
private fun Url(dialogState: HttpRequestDialogState) {
    FieldTitle(text = "Url")
    SmallSpacer()
    ShortXTextField(
        value = dialogState.url,
        onValueChange = {
            dialogState.url = it
        },
        showSymbolButton = true
    )
}

@Composable
private fun Method(dialogState: HttpRequestDialogState) {
    FieldTitle(text = "Method")
    val methodDropdownState = rememberDropdownSelectorState()
    SmallSpacer()
    Box(modifier = Modifier.fillMaxWidth()) {
        FilledTonalButton(onClick = {
            methodDropdownState.open()
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = dialogState.method.name)
                StandardSpacer()
                RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
            }
        }
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
    }
}

@Composable
private fun ColumnScope.ResponseAdapter(
    dialogState: HttpRequestDialogState,
) {
    val i18N = LocalI18N.current
    val context = LocalContext.current
    Column(modifier = Modifier) {
        FieldTitle(
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
                    Button(
                        onClick = {
                            if (dialogState.expressionInput.isNotBlank()) {
                                dialogState.addExpression(dialogState.expressionInput)
                            }
                        }) {
                        RemixIcon(
                            remixName = Remix.System.add_fill,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                showSymbolButton = false
            )

        }
    }
}

@Composable
private fun Headers(dialogState: HttpRequestDialogState) {
    FieldTitle(text = "Headers (Optional)")
    SmallSpacer()
    dialogState.headers.forEachIndexed { index, it ->
        ListItem(titleLines = listOf(it.key, it.value),
            icon = {
                RemixIcon(remixName = Remix.Logos.flickr_line)
            },
            action1 = {
                IconButton(onClick = {
                    dialogState.removeHeader(index)
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            })
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
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
        SmallSpacer()
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = valueInput,
            onValueChange = {
                valueInput = it
            },
            label = {
                Text(text = "Value")
            },
            showSymbolButton = false
        )
        SmallSpacer()
        Button(
            modifier = Modifier
                .fillMaxWidth(),
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
            Text(text = "Add header")
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


@Composable
private fun RequestBody(dialogState: HttpRequestDialogState) {
    FieldTitle(text = "Request body")
    SmallSpacer()

    val typeDropdownState = rememberDropdownSelectorState()
    Box(modifier = Modifier.fillMaxWidth()) {
        FilledTonalButton(onClick = {
            typeDropdownState.open()
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = dialogState.requestBody.label)
                StandardSpacer()
                RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
            }
        }
        DropdownSelector(
            state = typeDropdownState,
            items = HttpRequestBodyType.values().map {
                DropdownItem(
                    labelLines = listOf(it.label),
                    data = it
                )
            },
            onSelect = {
                dialogState.setRequestBodyType(it.data)
            })
    }

    StandardSpacer()

    AnimatedVisibility(visible = dialogState.requestBody == HttpRequestBodyType.Json) {
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = dialogState.requestBodyJson,
            onValueChange = {
                dialogState.requestBodyJson = it
            },
            label = {
                Text(text = "Json")
            },
            showSymbolButton = true
        )
    }

    AnimatedVisibility(visible = dialogState.requestBody == HttpRequestBodyType.Form) {
        Column {
            dialogState.requestBodyForm.forEachIndexed { index, it ->
                ListItem(titleLines = listOf(it.key, it.value),
                    icon = {
                        RemixIcon(remixName = Remix.Logos.flickr_line)
                    },
                    action1 = {
                        IconButton(onClick = {
                            dialogState.removeForm(index)
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    })
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
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
                SmallSpacer()
                ShortXTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = valueInput,
                    onValueChange = {
                        valueInput = it
                    },
                    label = {
                        Text(text = "Value")
                    },
                    showSymbolButton = false
                )
                SmallSpacer()
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        if (keyInput.isNotEmpty() && valueInput.isNotEmpty()) {
                            dialogState.addForm(
                                HttpRequestBodyFormItem.newBuilder()
                                    .setKey(keyInput)
                                    .setValue(valueInput)
                                    .build()
                            )
                        }
                    }) {
                    Text(text = "Add form item")
                }
            }
        }
    }
}