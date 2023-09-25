package tornaco.apps.shortx.ui.addrule.action.selector.http

import android.webkit.URLUtil
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyJson
import tornaco.apps.shortx.core.proto.common.HttpRequestHeader
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.curl.toHttpRequestMethod
import tornaco.apps.shortx.core.util.fallbackOnEmpty

/*
* POST /binding/api/genAuthKey HTTP/1.1
DS: 1694311009,2j5lc4,4e4248abeaaf0d4ac5e328af4ca6a98c
cookie: stuid=282679178;stoken=v2_nCRrKqGvIUgdOG8PC_5fmr9qXi7MiEIT7N30B3PJ-HDbZQ3uADBo1JeUuXNUgTOAht3F81eZ2l_PThnl6qwwS4qi4Nw=;mid=0c3pm41yyr_mhy;
x-rpc-client_type: 2
x-rpc-app_version: 2.52.1
x-rpc-sys_version: 13
x-rpc-channel: miyousheluodi
x-rpc-device_id: 2f52ab7d-1757-3561-9ed8-e4fb375688d9
x-rpc-device_fp: 38d7f015c16c1
x-rpc-device_name: OnePlus PHK110
x-rpc-device_model: PHK110
Referer: https://app.mihoyo.com
x-rpc-verify_key: bll8iq97cem8
Content-Type: application/json; charset=UTF-8
Content-Length: 84
Host: api-takumi.miyoushe.com
Connection: Keep-Alive
Accept-Encoding: gzip
User-Agent: okhttp/4.9.3

{"auth_appid":"im_ccs","game_biz":"hk4e_cn","game_uid":181588968,"region":"cn_gf01"}
* */

fun String.toHttpCanaryExportHcy(): HttpCanaryExportHcy {
    val lines = this.lines()
    val line1 = lines[0]
    val line1Splits = line1.split(" ").map { it.trim() }
    val method = line1Splits[0]
    val path = line1Splits[1]

    val emptyLineIndex = lines.indexOfLast {
        it.trim().isEmpty()
    }
    val body = if (emptyLineIndex > 0 && lines.size > emptyLineIndex + 1) {
        lines[emptyLineIndex + 1]
    } else null

    val headers = lines.subList(1, if (emptyLineIndex > 0) emptyLineIndex else lines.size - 1)
        .filterNot { it.isEmpty() }
        .map {
            val splits = it.split(": ")
            val k = splits[0]
            val v = splits[1]
            k to v
        }

    val host = headers.firstOrNull { it.first == "Host" }?.second?.let {
        if (URLUtil.isNetworkUrl(it)) {
            it
        } else {
            "https://$it"
        }
    }
        ?: throw Error("Can not locate host from headers: ${headers.joinToString(System.lineSeparator())}")

    return HttpCanaryExportHcy(
        method,
        host,
        path,
        headers,
        body
    )
}

data class HttpCanaryExportHcy(
    val method: String,
    val host: String,
    val path: String,
    val headers: List<Pair<String, String>>,
    val body: String?
) {
    val url get() = host + path
    val headerAsMap get() = headers.toMap()
}

fun HttpCanaryExportHcy.toHttpRequest(): HttpRequest {
    return HttpRequest.newBuilder()
        .setUrl(this.url.fallbackOnEmpty(""))
        .setMethod(this.method.fallbackOnEmpty("GET").toHttpRequestMethod())
        .addAllHeaders(mutableListOf<HttpRequestHeader>().apply {
            if (this@toHttpRequest.headers.isNotEmpty()) {
                this@toHttpRequest.headers.forEach {
                    add(HttpRequestHeader.newBuilder().setKey(it.first).setValue(it.second).build())
                }
            }
        })
        .apply {
            if (!this@toHttpRequest.body.isNullOrEmpty()) {
                requestBody = HttpRequestBodyJson.newBuilder().setJson(this@toHttpRequest.body).build()
                    .pack_()
            }
        }
        .build()
}