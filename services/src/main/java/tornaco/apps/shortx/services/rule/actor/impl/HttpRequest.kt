package tornaco.apps.shortx.services.rule.actor.impl

import com.google.gson.GsonBuilder
import com.google.protobuf.Message
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.mvel2.MVEL
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.HttpRequestHeaderBodyJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.HttpRequestJsonMapAdapter
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyForm
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyJson
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.util.concurrent.TimeUnit


object HttpRequest : Actor {
    private val logging = HttpLoggingInterceptor {
        logger.d(it)
    }.apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    private val client by lazy {
        OkHttpClient.Builder()
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(logging)
            .build()
    }

    private val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting().create()


    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.HttpRequest::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.HttpRequest::class.java
        val url = data.url.inflateContextMap(contextData)
            .replaceLocalVarValues(localVars)
            .replaceGlobalVarValues {
                shortXContext.rule.getAllGlobalVars()
            }
        logger.d("url: $url")

        val requestBody =
            data.requestBody.toHttpResponseBody(
                contextData,
                localVars
            ) { shortXContext.rule.getAllGlobalVars() }

        val response = callUrl(
            url = url,
            method = data.method.name,
            headers = data.headersList.associate {
                it.key to (it.value).inflateContextMap(
                    contextData
                ).replaceLocalVarValues(localVars)
                    .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
            },
            requestBody = requestBody,
        )

        handleResponseBody(
            response = response,
            adapter = data.adapter
        ).apply {
            if (data.adapter is_ HttpRequestJsonMapAdapter::class.java
                || data.adapter is_ HttpRequestHeaderBodyJsonMapAdapter::class.java
            ) {
                forEachIndexed { index, value ->
                    val key =
                        "${ContextDataMapping.HttpRequest.httpRequestRet.name}${index + 1}"
                    logger.d("Add context data: $key $value")
                    params.contextData.add(
                        key = key,
                        value = value
                    )
                }
            } else {
                val key = ContextDataMapping.HttpRequest.httpRequestRet.name
                val value = this[0]
                logger.d("Add context data: $key $value")
                params.contextData.add(
                    key = key,
                    value = value
                )
            }
        }
    }

    fun handleResponseBody(
        response: Response,
        adapter: ProtoAny
    ): List<String> {
        val responseBody = response.body?.string() ?: "NO-BODY"
        val responseHeaders = response.headers
        logger.p("responseBody: $responseBody")
        logger.p("responseHeaders: $responseHeaders")

        return if (adapter is_ HttpRequestJsonMapAdapter::class.java) {
            val jsonAdapter = adapter unpack_ HttpRequestJsonMapAdapter::class.java
            val jsonElement = Json.parseToJsonElement(responseBody)
            val map: Map<String, JsonElement> = jsonElement.jsonObject.toMap()
            logger.p("map: $map")
            jsonAdapter.expressionsList.map { expression ->
                logger.d("expression: $expression")
                val res = runCatching {
                    MVEL.executeExpression(
                        MVEL.compileExpression(expression),
                        map
                    )
                }.getOrElse {
                    logger.e(it, "expression item")
                    ""
                }
                val resAsString = if (res is JsonPrimitive) {
                    res.content
                } else {
                    res.toString()
                }
                logger.d("resAsString: $resAsString")
                resAsString
            }
        } else if (adapter is_ HttpRequestHeaderBodyJsonMapAdapter::class.java) {
            val jsonAdapter = adapter unpack_ HttpRequestHeaderBodyJsonMapAdapter::class.java
            val bodyJsonElement = Json.parseToJsonElement(responseBody)
            val bodyMap: Map<String, JsonElement> = bodyJsonElement.jsonObject.toMap()
            val headerMap = responseHeaders.toMap().mapKeys {
                it.key.fixSpecialLetters()
            }
            val dataMap = mapOf("headers" to headerMap, "body" to bodyMap)
            logger.p("dataMap: $dataMap")
            jsonAdapter.expressionsList.map { expression ->
                logger.d("expression: $expression")
                val fixedExpression = expression.fixSpecialLetters()
                logger.d("fixedExpression: $expression")
                val res = runCatching {
                    MVEL.executeExpression(
                        MVEL.compileExpression(fixedExpression),
                        dataMap
                    )
                }.getOrElse {
                    logger.e(it, "expression item")
                    ""
                }
                val resAsString = if (res is JsonPrimitive) {
                    res.content
                } else {
                    res.toString()
                }
                logger.d("resAsString: $resAsString")
                resAsString
            }
        } else {
            listOf(responseBody.trim())
        }
    }

    fun callUrl(
        url: String,
        method: String,
        headers: Map<String, String>,
        requestBody: RequestBody? = null,
    ): Response {
        logger.d("handleUrl: $url $method $headers $requestBody")

        val request = Request.Builder().url(url)
            .method(method, requestBody)
            .apply {
                headers.forEach { (k, v) ->
                    this.addHeader(k, v)
                }
            }
            .build()
        return client.newCall(request).execute()
    }

    fun ProtoAny.toHttpResponseBody(
        contextData: ContextData,
        localVars: LocalVars,
        globalVarsLazy: () -> List<GlobalVar>
    ): RequestBody? {
        return if (this is_ HttpRequestBodyJson::class.java) {
            val bodyJson = this unpack_ HttpRequestBodyJson::class.java
            bodyJson.json.inflateContextMap(contextData)
                .replaceLocalVarValues(localVars)
                .replaceGlobalVarValues(globalVarsLazy)
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        } else if (this is_ HttpRequestBodyForm::class.java) {
            val bodyForm = this unpack_ HttpRequestBodyForm::class.java
            FormBody.Builder().apply {
                bodyForm.itemsList.forEach {
                    add(
                        it.key,
                        it.value.inflateContextMap(contextData)
                            .replaceLocalVarValues(localVars)
                            .replaceGlobalVarValues(globalVarsLazy)
                    )
                }
            }.build()
        } else null
    }
}

fun String.fixSpecialLetters(): String {
    return this.replace("-", "_").replace(" ", "_")
}