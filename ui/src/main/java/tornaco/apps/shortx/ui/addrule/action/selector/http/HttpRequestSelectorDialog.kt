@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector.http

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberUpdatedState
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
import tornaco.apps.shortx.core.util.curl.curlCommandToHttpRequest
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.HttpRequestAdapter
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.addrule.action.selector.KeyValueEditDialog
import tornaco.apps.shortx.ui.addrule.action.selector.rememberKeyValueEditDialogState
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.ErrorMessageDialog
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
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.code.OutputDialog
import tornaco.apps.shortx.ui.base.code.rememberOutputDialogState
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberErrorMessageDialogState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils

internal enum class AdapterType {
    Raw, BodyJson, HeaderBodyJson
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
    internal var requestBodyJson by mutableStateOf(
        "{}"
    )
    internal var requestBodyForm by mutableStateOf<List<HttpRequestBodyFormItem>>(
        emptyList()
    )

    internal var expressions by mutableStateOf(listOf<String>())
    internal var responseAdapter by mutableStateOf(AdapterType.Raw)

    internal var isTesting by mutableStateOf(false)

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
            adapter = when (this.responseAdapter) {
                AdapterType.BodyJson -> {
                    HttpRequestAdapter.BodyJsonMap(expressions = this.expressions)
                }

                AdapterType.HeaderBodyJson -> {
                    HttpRequestAdapter.HeaderBodyJsonMap(expressions = this.expressions)
                }

                else -> {
                    HttpRequestAdapter.Raw
                }
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

        this.expressions = currentAction?.let {
            when (it.adapter) {
                is HttpRequestAdapter.BodyJsonMap -> {
                    it.adapter.expressions
                }

                is HttpRequestAdapter.HeaderBodyJsonMap -> {
                    it.adapter.expressions
                }

                else -> {
                    emptyList()
                }
            }
        } ?: emptyList()
        this.responseAdapter = currentAction?.let {
            when (it.adapter) {
                is HttpRequestAdapter.BodyJsonMap -> AdapterType.BodyJson
                is HttpRequestAdapter.HeaderBodyJsonMap -> AdapterType.HeaderBodyJson
                else -> AdapterType.Raw
            }
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

    fun addHeader(header: HttpRequestHeader, index: Int) {
        this.headers = this.headers.toMutableList().apply {
            add(index, header)
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

    fun addForm(item: HttpRequestBodyFormItem, index: Int) {
        this.requestBodyForm = this.requestBodyForm.toMutableList().apply {
            add(index, item)
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
    val context = LocalContext.current

    val outputDialogState = rememberOutputDialogState(title = "ui.action.http.request")
    OutputDialog(state = outputDialogState)

    val errorDialogState =
        rememberErrorMessageDialogState(title = LocalI18N.current["ui.common.error.view.message"])
    ErrorMessageDialog(state = errorDialogState)

    val curlInputDialogState =
        rememberTextInputState(
            title = LocalI18N.current["ui.action.http.request.from.curl"],
            message = LocalI18N.current["ui.action.http.request.from.curl.message"],
            onSelected = {
                runCatching {
                    val request = it.curlCommandToHttpRequest()
                    dialogState.show(request.pack_().toAction(shortXManager) as Action.HttpRequest?)
                }.onFailure {
                    composeLogger.e(it, "curlCommandToHttpRequest")
                    val errorMsg = it.stackTraceToString()
                    errorDialogState.show(errorMsg)
                }
            })
    TextInputDialog(state = curlInputDialogState)

    val httpCanaryJsonInputDialogState =
        rememberTextInputState(
            title = LocalI18N.current["ui.action.http.request.from.http.canary"],
            message = LocalI18N.current["ui.action.http.request.from.http.canary.message"],
            onSelected = {
                runCatching {
                    val request = it.toHttpCanaryExportJson().toHttpRequest()
                    dialogState.show(request.pack_().toAction(shortXManager) as Action.HttpRequest?)
                }.onFailure {
                    composeLogger.e(it, "toHttpCanaryExportJson")
                    errorDialogState.show(it.stackTraceToString())
                }
            })
    TextInputDialog(state = httpCanaryJsonInputDialogState)

    val httpCanaryHcyInputDialogState =
        rememberTextInputState(
            title = LocalI18N.current["ui.action.http.request.from.http.canary.hcy"],
            message = LocalI18N.current["ui.action.http.request.from.http.canary.hcy.message"],
            onSelected = {
                runCatching {
                    val request = it.toHttpCanaryExportHcy().toHttpRequest()
                    dialogState.show(request.pack_().toAction(shortXManager) as Action.HttpRequest?)
                }.onFailure {
                    composeLogger.e(it, "toHttpCanaryExportHcy")
                    errorDialogState.show(it.stackTraceToString())
                }
            })
    TextInputDialog(state = httpCanaryHcyInputDialogState)

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
                            Box {
                                val moreActionsDropdown = rememberDropdownSelectorState()
                                DropdownSelector(
                                    widthFraction = 0.5f,
                                    state = moreActionsDropdown,
                                    items = listOf(
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.action.http.request.from.curl"]),
                                            data = MoreMenuOptions.FromCurl
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.action.http.request.from.http.canary"]),
                                            data = MoreMenuOptions.FromHttpCanaryJson
                                        ),
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current["ui.action.http.request.from.http.canary.hcy"]),
                                            data = MoreMenuOptions.FromHttpCanaryHcy
                                        ),
                                    ),
                                    onSelect = {
                                        when (it.data) {
                                            MoreMenuOptions.FromCurl -> {
                                                curlInputDialogState.show()
                                            }

                                            MoreMenuOptions.FromHttpCanaryJson -> {
                                                httpCanaryJsonInputDialogState.show()
                                            }

                                            MoreMenuOptions.FromHttpCanaryHcy -> {
                                                httpCanaryHcyInputDialogState.show()
                                            }
                                        }
                                    })
                                IconButton(onClick = {
                                    moreActionsDropdown.open()
                                }) {
                                    RemixIcon(remixName = Remix.System.more_2_fill)
                                }
                            }

                            val scope = rememberCoroutineScope()
                            IconButton(
                                enabled = !dialogState.isTesting,
                                onClick = {
                                    if (dialogState.url.isNotBlank()) {
                                        val request = dialogState.httpRequest
                                        dialogState.isTesting = true
                                        scope.launch {
                                            val out = withContext(Dispatchers.IO) {
                                                shortXManager.executeHttpRequest(request.toProtoAction() as HttpRequest)
                                            }
                                            dialogState.isTesting = false
                                            outputDialogState.show(out.joinToString(System.lineSeparator()))
                                        }
                                    }
                                }) {
                                if (dialogState.isTesting) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                } else {
                                    RemixIcon(remixName = Remix.Media.play_line)
                                }
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
                        LargeSpacer()

                        Method(dialogState)
                        LargeSpacer()

                        Headers(dialogState)
                        LargeSpacer()

                        RequestBody(dialogState)
                        LargeSpacer()

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
                        id = AdapterType.BodyJson.name,
                        title = LocalI18N.current["ui.action.http.request.json"],
                        summary = LocalI18N.current["ui.action.http.request.json.summary"],
                    ),
                    MenuDialogItem(
                        id = AdapterType.HeaderBodyJson.name,
                        title = LocalI18N.current["ui.action.http.request.json.header.body"],
                        summary = LocalI18N.current["ui.action.http.request.json.summary.header.body"],
                    ),
                ),
                onItemSelected = { data, id ->
                    dialogState.responseAdapter = AdapterType.valueOf(id)
                }
            )
        MenuDialog(state = adapterSelectorState)
        FilledTonalButton(onClick = {
            adapterSelectorState.show()
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = dialogState.responseAdapter.let {
                    when (it) {
                        AdapterType.Raw -> {
                            LocalI18N.current["ui.action.http.request.raw"]
                        }

                        AdapterType.HeaderBodyJson -> {
                            LocalI18N.current["ui.action.http.request.json.header.body"]
                        }

                        AdapterType.BodyJson -> {
                            LocalI18N.current["ui.action.http.request.json"]
                        }
                    }
                })
                StandardSpacer()
                RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
            }
        }
        LargeSpacer()
    }

    StandardSpacer()

    AnimatedVisibility(visible = dialogState.responseAdapter == AdapterType.Raw) {
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

    AnimatedVisibility(
        visible = dialogState.responseAdapter == AdapterType.BodyJson
                || dialogState.responseAdapter == AdapterType.HeaderBodyJson
    ) {
        Column {
            TipRow(text = LocalI18N.current["ui.action.http.request.json.expression.description"])
            StandardSpacer()
            dialogState.expressions.forEachIndexed { index, s ->
                ExpressionItem(expression = s, index = index) {
                    dialogState.removeExpression(index)
                }
            }
            StandardSpacer()

            val mvelInputState = rememberTextInputState(
                title = LocalI18N.current["ui.action.http.request.json.expression"],
                message = null,
                onSelected = {
                    if (it.isNotBlank()) {
                        dialogState.addExpression(it)
                    }
                })
            TextInputDialog(state = mvelInputState)
            Button(
                modifier = Modifier,
                onClick = {
                    mvelInputState.show()
                }) {
                Text(text = "Add MVEL")
            }
        }
    }
}

@Composable
private fun Headers(dialogState: HttpRequestDialogState) {
    FieldTitle(text = "Headers (Optional)")
    SmallSpacer()

    val headerDialogState = rememberKeyValueEditDialogState("Header")
    KeyValueEditDialog(state = headerDialogState)

    dialogState.headers.forEachIndexed { index, it ->
        val updateIndex = rememberUpdatedState(newValue = index)
        val updateItem = rememberUpdatedState(newValue = it)
        ListItem(
            titleLines = listOf(it.key, it.value),
            icon = {
                RemixIcon(remixName = Remix.Logos.flickr_line)
            },
            action1 = {
                IconButton(onClick = {
                    dialogState.removeHeader(index)
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            },
            onClick = {
                headerDialogState.show(
                    key = updateItem.value.key,
                    value = updateItem.value.value,
                    callback = {
                        dialogState.removeHeader(index)
                        dialogState.addHeader(
                            HttpRequestHeader.newBuilder()
                                .setKey(it.first)
                                .setValue(it.second)
                                .build(),
                            updateIndex.value
                        )
                    })
            })
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Button(
            modifier = Modifier,
            onClick = {
                headerDialogState.show(
                    key = "",
                    value = "",
                    callback = {
                        if (it.first.isNotEmpty()) {
                            dialogState.addHeader(
                                HttpRequestHeader.newBuilder()
                                    .setKey(it.first)
                                    .setValue(it.second)
                                    .build()
                            )
                        }

                    }
                )
            }) {
            Text(text = "Add Header")
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
        val itemDialogState = rememberKeyValueEditDialogState("Form item")
        KeyValueEditDialog(state = itemDialogState)

        Column {
            dialogState.requestBodyForm.forEachIndexed { index, it ->
                val updateItem = rememberUpdatedState(newValue = it)
                val updateIndex = rememberUpdatedState(newValue = index)
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
                    },
                    onClick = {
                        itemDialogState.show(
                            key = updateItem.value.key,
                            value = updateItem.value.value,
                            callback = {
                                dialogState.removeForm(index)
                                dialogState.addForm(
                                    HttpRequestBodyFormItem.newBuilder()
                                        .setKey(it.first)
                                        .setValue(it.second)
                                        .build(),
                                    updateIndex.value
                                )
                            })
                    })
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Button(
                    modifier = Modifier,
                    onClick = {
                        itemDialogState.show(
                            key = "",
                            value = "",
                            callback = {
                                dialogState.addForm(
                                    HttpRequestBodyFormItem.newBuilder()
                                        .setKey(it.first)
                                        .setValue(it.second)
                                        .build()
                                )
                            })
                    }) {
                    Text(text = "Add Form item")
                }
            }
        }
    }
}

enum class MoreMenuOptions {
    FromCurl,
    FromHttpCanaryJson,
    FromHttpCanaryHcy,
}

