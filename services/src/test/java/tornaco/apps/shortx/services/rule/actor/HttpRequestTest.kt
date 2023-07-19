package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.proto.action.HttpRequestJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.HttpRequestRawAdapter
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyJson
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequest
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequest.toHttpRequestBody

class HttpRequestTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testJsonAdapter() {
        val res = HttpRequest.handleUrl(
            url = "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/profiles/forceidle.json",
            method = "GET",
            headers = emptyMap(),
            requestBody = null,
            adapter = HttpRequestJsonMapAdapter.newBuilder().addAllExpressions(listOf("author"))
                .build()
                .pack_(),
        )
        Assert.assertEquals(listOf("countrysideboy"), res)
    }

    @Test
    fun testJsonAdapter2() {
        val res = HttpRequest.handleUrl(
            url = "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/tag.json",
            method = "GET",
            headers = emptyMap(),
            requestBody = null,
            adapter = HttpRequestJsonMapAdapter.newBuilder().addAllExpressions(listOf("colors.red"))
                .build()
                .pack_(),
        )
        Assert.assertEquals(listOf("#FF5252"), res)
    }

    @Test
    fun testRawAdapter() {
        val res = HttpRequest.handleUrl(
            url = "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/tag.json",
            method = "GET",
            headers = emptyMap(),
            requestBody = null,
            adapter = HttpRequestRawAdapter.newBuilder().build().pack_()
        )
        Assert.assertEquals(
            res,
            listOf(
                """
            {
                "colors": {
                    "green": "#43A047",
                    "yellow": "#F57C00",
                    "red": "#FF5252"
                },
                "tags": {
                    "system_opt": {
                        "color": "yellow",
                        "zh_CN": "系统优化",
                        "zh": "系统优化",
                        "en": "System opt"
                    },
                    "app_opt": {
                        "color": "green",
                        "zh_CN": "App优化",
                        "zh": "App优化",
                        "en": "App opt"
                    }
                }
            }
        """.trimIndent()
            )
        )
    }

    @Test
    fun testRequestBody() {
        val res = HttpRequest.handleUrl(
            url = "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/tag.json",
            method = "POST",
            headers = mapOf("name" to "Nick"),
            requestBody = HttpRequestBodyJson.newBuilder().setJson(
                """
                {
                  "color": 10039203
                }
            """.trimIndent()
            ).build().pack_()
                .toHttpRequestBody(ContextData.obtain()) { emptyList() },
            adapter = HttpRequestRawAdapter.newBuilder().build().pack_()
        )

        println(res)
    }
}