package tornaco.apps.shortx.core.http

import okhttp3.Response
import tornaco.apps.shortx.core.proto.common.HttpResponseData


fun Response.toHttpResponseData(): HttpResponseData {
    return HttpResponseData.newBuilder()
        .setCode(this.code)
        .setMessage(this.message)
        .setBody(this.body?.string().orEmpty())
        .putAllHeaders(this.headers.toMultimap().mapValues { it.value.joinToString(", ") })
        .build()
}