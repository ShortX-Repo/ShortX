package tornaco.apps.shortx.ui.addrule.action.selector.http

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.common.HttpRequestHeader
import tornaco.apps.shortx.core.util.curl.toHttpRequestMethod
import tornaco.apps.shortx.core.util.fallbackOnEmpty

private val gson = GsonBuilder().disableHtmlEscaping().create()
fun String.toHttpCanaryExportJson() = gson.fromJson(this, HttpCanaryExportJson::class.java)

fun HttpCanaryExportJson.toHttpRequest(): HttpRequest {
    return HttpRequest.newBuilder()
        .setUrl(this.url.fallbackOnEmpty(""))
        .setMethod(this.method.fallbackOnEmpty("GET").toHttpRequestMethod())
        .addAllHeaders(mutableListOf<HttpRequestHeader>().apply {
            if (this@toHttpRequest.headers.isNotEmpty()) {
                this@toHttpRequest.headers.forEach {
                    add(HttpRequestHeader.newBuilder().setKey(it.key).setValue(it.value).build())
                }
            }
        })
        .build()
}

data class HttpCanaryExportJson(
    @field:SerializedName("app")
    val app: String? = null,

    @field:SerializedName("duration")
    val duration: String? = null,

    @field:SerializedName("headers")
    val headers: Map<String, String> = emptyMap(),

    @field:SerializedName("protocol")
    val protocol: String? = null,

    @field:SerializedName("remoteIp")
    val remoteIp: String? = null,

    @field:SerializedName("method")
    val method: String? = null,

    @field:SerializedName("remotePort")
    val remotePort: Int? = null,

    @field:SerializedName("sessionId")
    val sessionId: String? = null,

    @field:SerializedName("time")
    val time: String? = null,

    @field:SerializedName("url")
    val url: String? = null
)
