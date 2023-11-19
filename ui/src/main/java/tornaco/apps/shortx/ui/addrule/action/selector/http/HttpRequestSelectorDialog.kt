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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyFormItem
import tornaco.apps.shortx.core.proto.common.HttpRequestHeader
import tornaco.apps.shortx.core.proto.common.HttpRequestMethod
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.curl.curlCommandToHttpRequest
import tornaco.apps.shortx.core.util.formatJsonString
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.addrule.action.selector.KeyValueEditDialog
import tornaco.apps.shortx.ui.addrule.action.selector.rememberKeyValueEditDialogState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
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
import tornaco.apps.shortx.ui.base.ShortXCard
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.SwitchRow
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.settings.log.HttpLogActivity
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils

@AndroidEntryPoint
class HttpRequestSelectorActivity : BaseSelectorActivity<Action.HttpRequest>() {
    @Composable
    override fun BaseSelectorActivity<Action.HttpRequest>.SelectorContent(currentAction: Action.HttpRequest?) {
        HttpRequestSelector(currentAction)
    }
}

@Composable
private fun BaseSelectorActivity<Action.HttpRequest>.HttpRequestSelector(currentAction: Action.HttpRequest?) {
    val vm = hiltViewModel<HttpRequestSelectorViewModel>()
    LaunchedEffect(vm) {
        if (currentAction == null) {
            vm.resetState()
        } else {
            vm.init(currentAction)
        }
    }

    val selectorState by vm.state.collectAsState()

    val context = LocalContext.current
    val errorTitle = LocalI18N.current["ui.common.error.view.message"]

    val curlInputDialogState =
        rememberTextInputState(
            title = LocalI18N.current["ui.action.http.request.from.curl"],
            message = LocalI18N.current["ui.action.http.request.from.curl.message"],
            onSelected = {
                runCatching {
                    val request = it.curlCommandToHttpRequest()
                    vm.init(request.pack_().toAction(shortXManager) as Action.HttpRequest?)
                }.onFailure {
                    composeLogger.e(it, "curlCommandToHttpRequest")
                    val errorMsg = it.stackTraceToString()
                    TextDisplayActivity.start(
                        context = context,
                        title = errorTitle,
                        text = errorMsg
                    )
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
                    vm.init(request.pack_().toAction(shortXManager) as Action.HttpRequest?)
                }.onFailure {
                    composeLogger.e(it, "toHttpCanaryExportJson")
                    TextDisplayActivity.start(
                        context = context,
                        title = errorTitle,
                        text = it.stackTraceToString()
                    )
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
                    vm.init(request.pack_().toAction(shortXManager) as Action.HttpRequest?)
                }.onFailure {
                    composeLogger.e(it, "toHttpCanaryExportHcy")
                    TextDisplayActivity.start(
                        context = context,
                        title = errorTitle,
                        text = it.stackTraceToString()
                    )
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
                                cancelAndFinish()
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

                            IconButton(onClick = {
                                HttpLogActivity.Starter.start(context)
                            }) {
                                RemixIcon(remixName = Remix.Document.file_text_line)
                            }

                            val scope = rememberCoroutineScope()
                            val outputTitle = LocalI18N.current["ui.action.http.request"]

                            IconButton(
                                enabled = !selectorState.isTesting,
                                onClick = {
                                    if (selectorState.url.isNotBlank()) {
                                        val request = selectorState.httpRequest
                                        vm.setIsTesting(true)
                                        scope.launch {
                                            val out = withContext(Dispatchers.IO) {
                                                shortXManager.executeHttpRequest(request.toProtoAction() as HttpRequest)
                                            }
                                            vm.setIsTesting(false)

                                            TextDisplayActivity.start(
                                                context,
                                                title = outputTitle,
                                                text = out.joinToString(separator = System.lineSeparator(),
                                                    transform = { str ->
                                                        runCatching {
                                                            formatJsonString(str)
                                                        }.getOrElse { str }
                                                    })
                                            )
                                        }
                                    }
                                }) {
                                if (selectorState.isTesting) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                } else {
                                    RemixIcon(remixName = Remix.Media.play_line)
                                }
                            }

                            IconButton(onClick = {
                                if (selectorState.url.isNotBlank()) {
                                    setResultAndFinish(selectorState.httpRequest)
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
                        Url(selectorState.url) {
                            vm.setUrl(it)
                        }
                        LargeSpacer()

                        Method(method = selectorState.method) {
                            vm.setMethod(it)
                        }
                        LargeSpacer()

                        Headers(
                            headers = selectorState.headers,
                            removeHeader = { vm.removeHeader(it) },
                            addHeader = { vm.addHeader(it) },
                            addHeaderWithIndex = { h, i ->
                                vm.addHeader(
                                    h, i
                                )
                            })
                        LargeSpacer()

                        RequestBody(
                            requestBody = selectorState.requestBody,
                            requestBodyJson = selectorState.requestBodyJson,
                            updateRequestBodyJson = {
                                vm.setRequestBodyJson(it)
                            },
                            requestBodyForm = selectorState.requestBodyForm,
                            setRequestBodyType = {
                                vm.setRequestBodyType(it)
                            },
                            removeForm = {
                                vm.removeForm(it)
                            },
                            addForm = {
                                vm.addForm(it)
                            },
                            addFormWithIndex = { f, i ->
                                vm.addForm(f, i)
                            })
                        LargeSpacer()

                        ResponseAdapter(
                            responseAdapter = selectorState.responseAdapter,
                            update = {
                                vm.setResponseAdapter(it)
                            },
                            expressions = selectorState.expressions,
                            addExpression = {
                                vm.addExpression(it)
                            }, removeExpression = {
                                vm.removeExpression(it)
                            })
                        SectionSpacer()

                        FieldTitle(text = LocalI18N.current["ui.action.misc"])
                        SmallSpacer()
                        CookieJar(selectorState.withCookieJar) { vm.withCookieJar(it) }
                        LargeSpacer()

                        TrustAllCerts(selectorState.trustAllCerts) { vm.trustAllCerts(it) }
                        LargeSpacer()

                        SectionSpacer()
                        SuggestedNavEntry()
                    }
                }
            }
        }
    }

}

@Composable
fun CookieJar(withCookieJar: Boolean, update: (Boolean) -> Unit) {
    SwitchRow(
        text = LocalI18N.current["ui.action.http.request.with.cookie.jar"],
        isChecked = withCookieJar,
        onCheckChange = {
            update(it)
        })
    SmallSpacer()
    TipBody(text = LocalI18N.current["ui.action.http.request.with.cookie.jar.message"])
}

@Composable
fun TrustAllCerts(trustAllCerts: Boolean, update: (Boolean) -> Unit) {
    SwitchRow(
        text = LocalI18N.current["ui.action.http.request.trust.all.certs"],
        isChecked = trustAllCerts,
        onCheckChange = {
            update(it)
        })
    SmallSpacer()
    TipBody(text = LocalI18N.current["ui.action.http.request.trust.all.certs.message"])
}

@Composable
private fun Url(url: String, update: (String) -> Unit) {
    FieldTitle(text = "Url")
    SmallSpacer()
    ShortXTextField(
        modifier = Modifier.fillMaxWidth(),
        value = url,
        onValueChange = {
            update(it)
        },
        showSymbolButton = true
    )
}

@Composable
private fun Method(method: HttpRequestMethod, update: (HttpRequestMethod) -> Unit) {
    FieldTitle(text = "Method")
    val methodDropdownState = rememberDropdownSelectorState()
    SmallSpacer()
    Box(modifier = Modifier.fillMaxWidth()) {
        FilledTonalButton(onClick = {
            methodDropdownState.open()
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = method.name)
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
                update(it.data)
            })
    }
}

@Composable
private fun ColumnScope.ResponseAdapter(
    responseAdapter: AdapterType,
    update: (AdapterType) -> Unit,
    expressions: List<String>,
    addExpression: (String) -> Unit,
    removeExpression: (Int) -> Unit,
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
                    update(AdapterType.valueOf(id))
                }
            )
        MenuDialog(state = adapterSelectorState)
        FilledTonalButton(onClick = {
            adapterSelectorState.show()
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = responseAdapter.let {
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

    AnimatedVisibility(visible = responseAdapter == AdapterType.Raw) {
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
        visible = responseAdapter == AdapterType.BodyJson || responseAdapter == AdapterType.HeaderBodyJson
    ) {
        Column {
            TipRow(text = LocalI18N.current["ui.action.http.request.json.expression.description"])
            StandardSpacer()
            expressions.forEachIndexed { index, s ->
                ExpressionItem(expression = s, index = index) {
                    removeExpression(index)
                }
            }
            StandardSpacer()

            val mvelInputState = rememberTextInputState(
                title = LocalI18N.current["ui.action.http.request.json.expression"],
                message = null,
                onSelected = {
                    if (it.isNotBlank()) {
                        addExpression(it)
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
private fun Headers(
    headers: List<HttpRequestHeader>,
    removeHeader: (Int) -> Unit,
    addHeader: (HttpRequestHeader) -> Unit,
    addHeaderWithIndex: (HttpRequestHeader, Int) -> Unit,
) {
    FieldTitle(text = "Headers (Optional)")
    SmallSpacer()

    val headerDialogState = rememberKeyValueEditDialogState("Header")
    KeyValueEditDialog(state = headerDialogState)

    headers.forEachIndexed { index, it ->
        val updateIndex = rememberUpdatedState(newValue = index)
        val updateItem = rememberUpdatedState(newValue = it)
        ListItem(
            titleLines = listOf(it.key, it.value),
            icon = {
                RemixIcon(remixName = Remix.Logos.flickr_line)
            },
            action1 = {
                IconButton(onClick = {
                    removeHeader(index)
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            },
            onClick = {
                headerDialogState.show(
                    key = updateItem.value.key,
                    value = updateItem.value.value,
                    callback = {
                        removeHeader(index)
                        addHeaderWithIndex(
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
                            addHeader(
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
private fun RequestBody(
    requestBody: HttpRequestBodyType,
    requestBodyJson: String,
    updateRequestBodyJson: (String) -> Unit,
    requestBodyForm: List<HttpRequestBodyFormItem>,
    setRequestBodyType: (HttpRequestBodyType) -> Unit,
    removeForm: (Int) -> Unit,
    addForm: (item: HttpRequestBodyFormItem) -> Unit,
    addFormWithIndex: (item: HttpRequestBodyFormItem, index: Int) -> Unit,
) {
    FieldTitle(text = "Request body")
    SmallSpacer()

    val typeDropdownState = rememberDropdownSelectorState()
    Box(modifier = Modifier.fillMaxWidth()) {
        FilledTonalButton(onClick = {
            typeDropdownState.open()
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = requestBody.label)
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
                setRequestBodyType(it.data)
            })
    }

    StandardSpacer()

    AnimatedVisibility(visible = requestBody == HttpRequestBodyType.Json) {
        ShortXTextField(
            modifier = Modifier.fillMaxWidth(),
            value = requestBodyJson,
            onValueChange = {
                updateRequestBodyJson(it)
            },
            label = {
                Text(text = "Json")
            },
            showSymbolButton = true
        )
    }

    AnimatedVisibility(visible = requestBody == HttpRequestBodyType.Form) {
        val itemDialogState = rememberKeyValueEditDialogState("Form item")
        KeyValueEditDialog(state = itemDialogState)

        Column {
            requestBodyForm.forEachIndexed { index, it ->
                val updateItem = rememberUpdatedState(newValue = it)
                val updateIndex = rememberUpdatedState(newValue = index)
                ListItem(titleLines = listOf(it.key, it.value),
                    icon = {
                        RemixIcon(remixName = Remix.Logos.flickr_line)
                    },
                    action1 = {
                        IconButton(onClick = {
                            removeForm(index)
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    onClick = {
                        itemDialogState.show(
                            key = updateItem.value.key,
                            value = updateItem.value.value,
                            callback = {
                                removeForm(index)
                                addFormWithIndex(
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
                                addForm(
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

