package tornaco.apps.shortx.core.util.curl

import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyForm
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyFormItem
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyJson
import tornaco.apps.shortx.core.proto.common.HttpRequestHeader
import tornaco.apps.shortx.core.proto.common.HttpRequestMethod
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty

fun String.curlCommandToHttpRequest(): HttpRequest {
    val parser = BasicCurlParser()
    val request = parser.parse(this)
    return HttpRequest.newBuilder()
        .setUrl(request.url.fallbackOnEmpty(""))
        .setMethod(request.method.toHttpRequestMethod())
        .addAllHeaders(mutableListOf<HttpRequestHeader?>().apply {
            request.headers.forEach { headerPair ->
                add(
                    HttpRequestHeader.newBuilder().setKey(headerPair.key).setValue(headerPair.value)
                        .build()
                )
            }
        })
        .apply {
            if (!request.postData.isNullOrEmpty()) {
                requestBody =
                    HttpRequestBodyJson.newBuilder().setJson(request.postData).build().pack_()
            } else if (request.formData.isNotEmpty()) {
                requestBody = HttpRequestBodyForm.newBuilder()
                    .addAllItems(
                        request.formData.map { pair ->
                            HttpRequestBodyFormItem.newBuilder().setKey(pair.key)
                                .setValue(pair.value.name).build()
                        }
                    )
                    .build().pack_()
            }
        }
        .build()
}

fun String.toHttpRequestMethod(): HttpRequestMethod {
    return HttpRequestMethod.valueOf(this)
}