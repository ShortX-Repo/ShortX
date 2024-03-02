package tornaco.apps.shortx.core.http

import android.annotation.SuppressLint
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import tornaco.apps.shortx.core.http.persistentcookiejar.PersistentCookieJar
import tornaco.apps.shortx.core.http.persistentcookiejar.cache.SetCookieCache
import tornaco.apps.shortx.core.http.persistentcookiejar.persistence.CookiePersistor
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyForm
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyFormItem
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyJson
import tornaco.apps.shortx.core.proto.common.HttpResponseData
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.emptyRuleContextAndVars
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class HttpCallerImpl(
    private val cookiePersistor: CookiePersistor,
    private val httpLogger: (String) -> Unit
) {
    private val logger = Logger("HttpCallerImpl")

    private lateinit var cookieJar: PersistentCookieJar

    private val logging =
        HttpLoggingInterceptor {
            httpLogger(it)
        }.apply { setLevel(HttpLoggingInterceptor.Level.BODY) }

    private lateinit var client: OkHttpClient
    private lateinit var clientWithCookieJar: OkHttpClient
    private lateinit var clientTrustAllCerts: OkHttpClient
    private lateinit var clientWithCookieJarTrustAllCerts: OkHttpClient

    init {
        initInternal()
    }

    private fun initInternal() {
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

    fun call(
        url: String,
        method: String,
        headers: MutableMap<String, String>,
        requestBodyData: ByteArrayWrapper?,
        withCookieJar: Boolean,
        trustAllCerts: Boolean
    ): HttpResponseData {
        val requestBodyProto = requestBodyData?.byteData?.let {
            ProtoAny.parseFrom(it)
        }
        val requestBody = requestBodyProto?.toHttpRequestBody(emptyRuleContextAndVars)
        logger.p("call: $url $method $headers $requestBody")
        val response = callUrl(url, method, headers, requestBody, withCookieJar, trustAllCerts)
        return response.toHttpResponseData()
    }

    fun callUrl(
        url: String,
        method: String,
        headers: Map<String, String>,
        requestBody: RequestBody? = null,
        withCookieJar: Boolean = true,
        trustAllCerts: Boolean = false,
    ): Response {
        logger.p("callUrl: $url $method $headers $requestBody")

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
}

fun ProtoAny.toHttpRequestBody(
    contextAndVars: RuleContextAndVars,
): RequestBody? {
    return if (this is_ HttpRequestBodyJson::class.java) {
        val bodyJson = this unpack_ HttpRequestBodyJson::class.java
        bodyJson.json.compileContextAndVars(contextAndVars)
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    } else if (this is_ HttpRequestBodyForm::class.java) {
        val bodyForm = this unpack_ HttpRequestBodyForm::class.java
        FormBody.Builder().apply {
            bodyForm.itemsList.forEach {
                add(
                    it.key.compileContextAndVars(contextAndVars),
                    it.value.compileContextAndVars(contextAndVars)
                )
            }
        }.build()
    } else null
}


fun ProtoAny.compileHttpRequestBody(
    contextAndVars: RuleContextAndVars,
): ProtoAny? {
    return if (this is_ HttpRequestBodyJson::class.java) {
        val bodyJson = this unpack_ HttpRequestBodyJson::class.java
        val compiledJson = bodyJson.json.compileContextAndVars(contextAndVars)
        HttpRequestBodyJson.newBuilder().setJson(compiledJson).build().pack_()
    } else if (this is_ HttpRequestBodyForm::class.java) {
        val bodyForm = this unpack_ HttpRequestBodyForm::class.java
        HttpRequestBodyForm.newBuilder()
            .apply {
                bodyForm.itemsList.forEach {
                    addItems(HttpRequestBodyFormItem.newBuilder()
                        .setKey(it.key.compileContextAndVars(contextAndVars))
                        .setValue(it.value.compileContextAndVars(contextAndVars))
                        .build())
                }
            }
            .build().pack_()
    } else null
}