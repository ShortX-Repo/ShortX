package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import com.google.protobuf.Message
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import okhttp3.Response
import org.mvel2.MVEL
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.http.HttpCallerImpl
import tornaco.apps.shortx.core.http.persistentcookiejar.persistence.SettingsStoreCookiePersistor
import tornaco.apps.shortx.core.http.toHttpRequestBody
import tornaco.apps.shortx.core.proto.action.HttpRequestHeaderBodyJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.HttpRequestJsonMapAdapter
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.data.SettingsStore
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.util.TestUtils


object HttpRequest : Actor {
    lateinit var callerImpl: HttpCallerImpl

    private val httpLogger: (String) -> Unit = {
        if (!TestUtils.isUnitTestEnv && HttpRequestLogging.isLogEnabled) {
            HttpRequestLogging.log(it)
        } else {
            logger.p(it)
        }
    }

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        val persistor = SettingsStoreCookiePersistor(
            SettingsStore(
                androidContext,
                "PersistentCookieJar",
                shortXContext.rule.systemServiceScope
            )
        )
        this.callerImpl = HttpCallerImpl(persistor, httpLogger)
        logger.d("callerImpl: $callerImpl")
    }

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.HttpRequest::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.HttpRequest::class.java
        val cv = RuleContextAndVars(params)

        val url = data.url.compileContextAndVars(cv)
        logger.d("url: $url")

        val requestBody = data.requestBody.toHttpRequestBody(cv)

        HttpRequestLogging.section("START")

        val response = callerImpl.callUrl(
            url = url,
            method = data.method.name,
            headers = data.headersList.associate {
                it.key.compileContextAndVars(cv) to (it.value).compileContextAndVars(cv)
            },
            requestBody = requestBody,
            withCookieJar = data.withCookieJar,
            trustAllCerts = data.trustAllCerts
        )

        HttpRequestLogging.section("END")

        handleResponseBody(
            cv,
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
        contextAndVars: RuleContextAndVars,
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
                val fixedExpression = expression
                    .compileContextAndVars(contextAndVars)
                    .fixSpecialLetters()
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

}

fun String.fixSpecialLetters(): String {
    return this.replace("-", "_").replace(" ", "_")
}