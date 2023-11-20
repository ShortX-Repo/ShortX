package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import com.android.internal.annotations.VisibleForTesting
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
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.data.SettingsStore
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.util.TestUtils
import tornaco.apps.shortx.services.util.http.persistentcookiejar.PersistentCookieJar
import tornaco.apps.shortx.services.util.http.persistentcookiejar.cache.SetCookieCache
import tornaco.apps.shortx.services.util.http.persistentcookiejar.persistence.CookiePersistor
import tornaco.apps.shortx.services.util.http.persistentcookiejar.persistence.SettingsStoreCookiePersistor
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager


object HttpRequest : Actor {
    private lateinit var cookieJar: PersistentCookieJar

    private val logging =
        HttpLoggingInterceptor {
            if (!TestUtils.isUnitTestEnv && HttpRequestLogging.isLogEnabled) {
                HttpRequestLogging.log(it)
            } else {
                logger.d(it)
            }
        }.apply { setLevel(HttpLoggingInterceptor.Level.BODY) }

    private lateinit var client: OkHttpClient
    private lateinit var clientWithCookieJar: OkHttpClient
    private lateinit var clientTrustAllCerts: OkHttpClient
    private lateinit var clientWithCookieJarTrustAllCerts: OkHttpClient

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        init(
            SettingsStoreCookiePersistor(
                SettingsStore(
                    androidContext,
                    "PersistentCookieJar",
                    shortXContext.rule.systemServiceScope
                )
            )
        )
    }

    @VisibleForTesting
    fun init(cookiePersistor: CookiePersistor) {
        val trustAllCerts = arrayOf<X509TrustManager>(@SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<out java.security.cert.X509Certificate>?,
                authType: String?
            ) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<out java.security.cert.X509Certificate>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        cookieJar = PersistentCookieJar(
            SetCookieCache(),
            cookiePersistor
        )
        logger.d("cookieJar: $cookieJar")

        clientWithCookieJar = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(logging)
            .build()
        logger.d("clientWithCookieJar: $cookieJar")

        client = OkHttpClient.Builder()
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(logging)
            .build()
        logger.d("client: $cookieJar")

        clientWithCookieJarTrustAllCerts = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
            .hostnameVerifier { _, _ -> true }
            .cookieJar(cookieJar)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(logging)
            .build()
        logger.d("clientWithCookieJarTrustAllCerts: $cookieJar")

        clientTrustAllCerts = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0])
            .hostnameVerifier { _, _ -> true }
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(logging)
            .build()
        logger.d("clientTrustAllCerts: $cookieJar")
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

        HttpRequestLogging.section("START")

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
            withCookieJar = data.withCookieJar,
            trustAllCerts = data.trustAllCerts
        )

        HttpRequestLogging.section("END")

        handleResponseBody(
            contextData = contextData,
            globalVarsLazy = { shortXContext.rule.getAllGlobalVars() },
            localVars = localVars,
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
        contextData: ContextData,
        globalVarsLazy: () -> List<GlobalVar>,
        localVars: LocalVars,
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
                    .inflateContextMap(contextData)
                    .replaceGlobalVarValues(globalVarsLazy)
                    .replaceLocalVarValues(localVars)
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

    fun callUrl(
        url: String,
        method: String,
        headers: Map<String, String>,
        requestBody: RequestBody? = null,
        withCookieJar: Boolean = true,
        trustAllCerts: Boolean = false,
    ): Response {
        logger.p("handleUrl: $url $method $headers $requestBody")

        val request = Request.Builder().url(url)
            .method(method, requestBody)
            .apply {
                headers.forEach { (k, v) ->
                    this.addHeader(k, v)
                }
            }
            .build()

        val client = if (withCookieJar) {
            if (trustAllCerts) {
                logger.d("Choose clientWithCookieJarTrustAllCerts")
                clientWithCookieJarTrustAllCerts
            } else {
                logger.d("Choose clientWithCookieJar")
                clientWithCookieJar
            }
        } else {
            if (trustAllCerts) {
                logger.d("Choose clientTrustAllCerts")
                clientTrustAllCerts
            } else {
                logger.d("Choose client")
                client
            }
        }

        return client.newCall(request).execute()
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

fun String.fixSpecialLetters(): String {
    return this.replace("-", "_").replace(" ", "_")
}