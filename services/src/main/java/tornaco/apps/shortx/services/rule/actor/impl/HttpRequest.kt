package tornaco.apps.shortx.services.rule.actor.impl

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
import okhttp3.logging.HttpLoggingInterceptor
import org.mvel2.MVEL
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.localvar.LocalVars
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
            data.requestBody.toHttpRequestBody(
                contextData,
                localVars
            ) { shortXContext.rule.getAllGlobalVars() }
        handleUrl(
            url = url,
            method = data.method.name,
            headers = data.headersList.associate {
                it.key to (it.value).inflateContextMap(
                    contextData
                ).replaceLocalVarValues(localVars)
                    .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
            },
            requestBody = requestBody,
            adapter = data.adapter
        ).apply {
            if (data.adapter is_ HttpRequestJsonMapAdapter::class.java) {
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

    fun handleUrl(
        url: String,
        method: String,
        headers: Map<String, String>,
        requestBody: RequestBody? = null,
        adapter: ProtoAny
    ): List<String> {
        logger.d("handleUrl: $url $method $headers $requestBody")

        val request = Request.Builder().url(url)
            .method(method, requestBody)
            .apply {
                headers.forEach { (k, v) ->
                    this.addHeader(k, v)
                }
            }
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: "NO-BODY"

        responseBody.apply {
            logger.d("response: $this")
            return if (adapter is_ HttpRequestJsonMapAdapter::class.java) {
                val jsonAdapter = adapter unpack_ HttpRequestJsonMapAdapter::class.java
                val jsonElement = Json.parseToJsonElement(this)
                val map: Map<String, JsonElement> = jsonElement.jsonObject.toMap()
                logger.d("map: $map")
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
            } else {
                listOf(this.trim())
            }
        }
    }

    fun ProtoAny.toHttpRequestBody(
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