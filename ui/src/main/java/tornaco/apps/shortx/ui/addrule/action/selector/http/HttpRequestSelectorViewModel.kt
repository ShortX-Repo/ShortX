package tornaco.apps.shortx.ui.addrule.action.selector.http

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyForm
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyFormItem
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyJson
import tornaco.apps.shortx.core.proto.common.HttpRequestHeader
import tornaco.apps.shortx.core.proto.common.HttpRequestMethod
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.HttpRequestAdapter
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

enum class AdapterType {
    Raw, BodyJson, HeaderBodyJson
}

enum class HttpRequestBodyType(val label: String) {
    Json("Json"),
    Form("Form"),
    None("None")
}

data class HttpRequestDialogState(
    val url: String = "",
    val method: HttpRequestMethod = HttpRequestMethod.GET,
    val headers: List<HttpRequestHeader> = emptyList(),

    val requestBody: HttpRequestBodyType = HttpRequestBodyType.None,

    val requestBodyJson: String = "",
    val requestBodyForm: List<HttpRequestBodyFormItem> = emptyList(),

    val expressions: List<String> = emptyList(),
    val responseAdapter: AdapterType = AdapterType.Raw,

    val withCookieJar: Boolean = false,
    val trustAllCerts: Boolean = false,
    val executeInAppProcess: Boolean = false,
    val isTesting: Boolean = false,
) {
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
            },
            withCookieJar = this.withCookieJar,
            trustAllCerts = this.trustAllCerts,
            executeInAppProcess = this.executeInAppProcess
        )
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class HttpRequestSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<HttpRequestDialogState>(context, {
        HttpRequestDialogState()
    }) {

    fun init(currentAction: Action.HttpRequest? = null) {
        currentAction?.let {
            updateState {
                copy(
                    url = currentAction.url,
                    method = currentAction.method,
                    headers = currentAction.headers,
                    withCookieJar = currentAction.withCookieJar,
                    trustAllCerts = currentAction.trustAllCerts,
                    executeInAppProcess = currentAction.executeInAppProcess,
                    expressions = currentAction.let {
                        when (it.adapter) {
                            is HttpRequestAdapter.BodyJsonMap -> {
                                (it.adapter as HttpRequestAdapter.BodyJsonMap).expressions
                            }

                            is HttpRequestAdapter.HeaderBodyJsonMap -> {
                                (it.adapter as HttpRequestAdapter.HeaderBodyJsonMap).expressions
                            }

                            else -> {
                                emptyList()
                            }
                        }
                    },
                    responseAdapter = currentAction.let {
                        when (it.adapter) {
                            is HttpRequestAdapter.BodyJsonMap -> AdapterType.BodyJson
                            is HttpRequestAdapter.HeaderBodyJsonMap -> AdapterType.HeaderBodyJson
                            else -> AdapterType.Raw
                        }
                    }
                )
            }
        }

        currentAction?.requestBody?.let {
            if (it is_ HttpRequestBodyJson::class.java) {
                val bodyJson = it unpack_ HttpRequestBodyJson::class.java
                updateState {
                    copy(
                        requestBody = HttpRequestBodyType.Json,
                        requestBodyJson = bodyJson.json,
                    )
                }
            } else if (it is_ HttpRequestBodyForm::class.java) {
                val bodyForm = it unpack_ HttpRequestBodyForm::class.java
                updateState {
                    copy(
                        requestBody = HttpRequestBodyType.Form,
                        requestBodyForm = bodyForm.itemsList
                    )
                }
            } else {
                updateState {
                    copy(
                        requestBody = HttpRequestBodyType.None,
                        requestBodyForm = emptyList(),
                        requestBodyJson = "{}"
                    )
                }
            }
        }

    }


    fun setMethod(method: HttpRequestMethod) {
        updateState {
            copy(
                method = method
            )
        }
    }

    fun addHeader(header: HttpRequestHeader) {
        updateState {
            copy(
                headers = this.headers.toMutableList().apply {
                    add(header)
                }
            )
        }
    }

    fun addHeader(header: HttpRequestHeader, index: Int) {
        updateState {
            copy(
                headers = this.headers.toMutableList().apply {
                    add(index, header)
                }
            )
        }
    }


    fun removeHeader(index: Int) {
        updateState {
            copy(
                headers = this.headers.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
    }

    fun addExpression(expression: String) {
        updateState {
            copy(
                expressions = this.expressions.toMutableList().apply {
                    add(expression)
                }
            )
        }
    }

    fun removeExpression(expression: Int) {
        updateState {
            copy(
                expressions = this.expressions.toMutableList().apply {
                    removeAt(expression)
                }
            )
        }
    }

    fun setRequestBodyType(bodyType: HttpRequestBodyType) {
        updateState {
            copy(
                requestBody = bodyType,
            )
        }
        if (bodyType != HttpRequestBodyType.None && state.value.method == HttpRequestMethod.GET) {
            updateState {
                copy(
                    method = HttpRequestMethod.POST
                )
            }
        }
    }

    fun addForm(item: HttpRequestBodyFormItem) {
        updateState {
            copy(
                requestBodyForm = this.requestBodyForm.toMutableList().apply {
                    add(item)
                }
            )
        }
    }

    fun addForm(item: HttpRequestBodyFormItem, index: Int) {
        updateState {
            copy(
                requestBodyForm = this.requestBodyForm.toMutableList().apply {
                    add(index, item)
                }
            )
        }
    }

    fun removeForm(index: Int) {
        updateState {
            copy(
                requestBodyForm = this.requestBodyForm.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
    }

    fun setIsTesting(boolean: Boolean) {
        updateState { copy(isTesting = boolean) }
    }

    fun setUrl(url: String) {
        updateState { copy(url = url) }
    }

    fun trustAllCerts(boolean: Boolean) {
        updateState { copy(trustAllCerts = boolean) }
    }

    fun executeInApp(boolean: Boolean) {
        updateState { copy(executeInAppProcess = boolean) }
    }

    fun withCookieJar(boolean: Boolean) {
        updateState { copy(withCookieJar = boolean) }
    }

    fun setRequestBodyJson(json: String) {
        updateState { copy(requestBodyJson = json) }
    }

    fun setResponseAdapter(responseAdapter: AdapterType) {
        updateState { copy(responseAdapter = responseAdapter) }
    }
}