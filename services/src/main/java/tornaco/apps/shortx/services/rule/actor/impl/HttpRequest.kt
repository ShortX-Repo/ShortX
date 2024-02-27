package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.protobuf.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.mvel2.MVEL
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.http.HttpCallerImpl
import tornaco.apps.shortx.core.http.IHttpCaller
import tornaco.apps.shortx.core.http.persistentcookiejar.persistence.SettingsStoreCookiePersistor
import tornaco.apps.shortx.core.http.toHttpRequestBody
import tornaco.apps.shortx.core.http.toHttpResponseData
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.action.HttpRequestHeaderBodyJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.HttpRequestJsonMapAdapter
import tornaco.apps.shortx.core.proto.common.HttpResponseData
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.data.SettingsStore
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.util.AppServiceProxy
import tornaco.apps.shortx.services.util.TestUtils
import java.time.Duration
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference


object HttpRequest : Actor {
    lateinit var localCallerImpl: HttpCallerImpl

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
        this.localCallerImpl = HttpCallerImpl(persistor, httpLogger)
        logger.d("localCallerImpl: $localCallerImpl")
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

        HttpRequestLogging.section("START")
        val response = executeHttpRequest(androidContext, cv, data)
        HttpRequestLogging.section("END")

        handleResponseBody(
            contextAndVars = cv,
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

    private suspend fun executeHttpRequest(
        context: Context,
        cv: RuleContextAndVars,
        data: tornaco.apps.shortx.core.proto.action.HttpRequest
    ): HttpResponseData {
        val url = data.url.compileContextAndVars(cv)
        logger.d("url: $url")
        val method = data.method.name
        val headers = data.headersList.associate {
            it.key.compileContextAndVars(cv) to (it.value).compileContextAndVars(cv)
        }
        val withCookieJar = data.withCookieJar
        val trustAllCerts = data.trustAllCerts

        return if (data.executeInAppProcess) {
            logger.i("execute in app process.")
            withContext(Dispatchers.IO) {
                val response = AppHttpRequestServiceProxy.call(
                    context = context,
                    scope = this,
                    url = url,
                    method = method,
                    headers = headers,
                    // TODO Inflate CV
                    requestBodyData = data.requestBody?.let {
                        ByteArrayWrapper(it.toByteArray())
                    },
                    withCookieJar = withCookieJar,
                    trustAllCerts = trustAllCerts
                )
                response ?: error("Can not get http response from App")
            }
        } else {
            logger.i("execute in system process.")
            val requestBody = data.requestBody.toHttpRequestBody(cv)
            val response = localCallerImpl.callUrl(
                url = url,
                method = method,
                headers = headers,
                requestBody = requestBody,
                withCookieJar = withCookieJar,
                trustAllCerts = trustAllCerts
            )
            response.toHttpResponseData()
        }
    }

    fun handleResponseBody(
        contextAndVars: RuleContextAndVars,
        response: HttpResponseData,
        adapter: ProtoAny
    ): List<String> {
        val responseBody = response.body ?: "NO-BODY"
        val responseHeaders = response.headersMap
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
            val headerMap = responseHeaders.mapKeys {
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

internal class AppHttpRequestServiceProxy(context: Context, intent: Intent, scope: CoroutineScope) :
    AppServiceProxy(context, intent, scope) {
    private val logger = tornaco.apps.shortx.core.util.Logger("AppHttpRequestServiceProxy")
    private var impl: IHttpCaller? = null

    override fun onConnected(binder: IBinder) {
        impl = IHttpCaller.Stub.asInterface(binder)
    }

    suspend fun call(
        url: String,
        method: String,
        headers: Map<String, String>,
        requestBodyData: ByteArrayWrapper?,
        withCookieJar: Boolean,
        trustAllCerts: Boolean
    ): HttpResponseData? {
        logger.d("call.")
        val res: AtomicReference<HttpResponseData> = AtomicReference()
        val requestId = HttpRequestSerialNum.nextId().toString()
        // Minutes
        val timeout = 2L

        setTask(object : ProxyTask {
            override suspend fun run() {
                runCatching {
                    logger.d("Before call exe.")
                    val receiver = SynchronousResultReceiver.get()
                    requireNotNull(impl) { "Impl is null." }.call(
                        requestId,
                        receiver,
                        url,
                        method,
                        headers,
                        requestBodyData,
                        withCookieJar,
                        trustAllCerts
                    )
                    logger.d("After call exe.")
                    val receiverRes = receiver.awaitResultNoInterrupt(Duration.ofMinutes(timeout))
                    logger.d("receiver get res: $receiverRes")
                    val byteRes: ByteArrayWrapper? = receiverRes.getValue(null)
                    res.set(
                        if (byteRes == null) {
                            null
                        } else {
                            HttpResponseData.parseFrom(byteRes.byteData)
                        }
                    )
                }.onFailure {
                    logger.e(it, "ProxyTask error")
                    if (it is CancellationException) {
                        logger.i("Shutdown http request: $requestId since it is CancellationException.")
                        impl?.close(requestId)
                    }
                    res.set(null)
                }
            }

        })
        logger.d("Before waitForCompletion.")
        waitForCompletion(Duration.ofMinutes(timeout))
        logger.d("After waitForCompletion.")
        return res.get()
    }

    companion object {
        suspend fun call(
            context: Context,
            scope: CoroutineScope,
            url: String,
            method: String,
            headers: Map<String, String>,
            requestBodyData: ByteArrayWrapper?,
            withCookieJar: Boolean,
            trustAllCerts: Boolean
        ): HttpResponseData? {
            val intent = Intent()
            intent.setPackage(BuildProp.APP_PACKAGE_NAME)
            intent.component =
                ComponentName.unflattenFromString("${BuildProp.APP_PACKAGE_NAME}/${BuildProp.APP_HTTP_REQUEST_SERVICE_NAME}")
            return AppHttpRequestServiceProxy(context, intent, scope).call(
                url,
                method,
                headers,
                requestBodyData,
                withCookieJar,
                trustAllCerts
            )
        }
    }

    object HttpRequestSerialNum {
        private val _id = AtomicInteger(0)

        fun nextId() = _id.getAndIncrement()
    }
}