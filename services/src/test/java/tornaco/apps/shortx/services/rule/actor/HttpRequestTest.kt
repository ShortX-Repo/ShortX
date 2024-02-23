package tornaco.apps.shortx.services.rule.actor

import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.http.HttpCallerImpl
import tornaco.apps.shortx.core.http.toHttpRequestBody
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.HttpRequestHeaderBodyJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.HttpRequestJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.HttpRequestRawAdapter
import tornaco.apps.shortx.core.proto.common.HttpRequestBodyJson
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.StringVar
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequest.handleResponseBody
import tornaco.apps.shortx.services.rule.actor.impl.fixSpecialLetters
import tornaco.apps.shortx.services.util.TestUtils

class HttpRequestTest {
    private lateinit var httpCaller: HttpCallerImpl

    @Before
    fun installLogger() {
        TestUtils.isUnitTestEnv = true
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
        httpCaller = HttpCallerImpl(mockk(relaxed = true)) {
            println(it)
        }
    }

    @Test
    fun testJsonAdapter() {
        val res = httpCaller.callUrl(
            url = "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/profiles/forceidle.json",
            method = "GET",
            headers = emptyMap(),
            requestBody = null,
        ).let {
            val cv = tornaco.apps.shortx.core.rule.RuleContextAndVars(
                contextData = ContextData.obtain(),
                globalVarsLazy = { emptyList() },
                localVars = LocalVars.obtain(),
            )
            handleResponseBody(
                cv,
                response = it,
                adapter = HttpRequestJsonMapAdapter.newBuilder().addAllExpressions(listOf("author"))
                    .build()
                    .pack_()
            )
        }
        Assert.assertEquals(listOf("countrysideboy"), res)
    }

    @Test
    fun testJsonAdapterWithHeaderAndBody() {
        val res = httpCaller.callUrl(
            url = "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/profiles/forceidle.json",
            method = "GET",
            headers = emptyMap(),
            requestBody = null,
        ).let {
            val cv = tornaco.apps.shortx.core.rule.RuleContextAndVars(
                contextData = ContextData.obtain(),
                globalVarsLazy = {
                    listOf(
                        GlobalVar.newBuilder()
                            .setType(StringVar.getDefaultInstance().pack_())
                            .setName("exp_test")
                            .setVar(
                                StringVar.newBuilder().setValue("headers.content-type").build()
                                    .pack_()
                            )
                            .build()
                    )
                },
                localVars = LocalVars.obtain(),
            )
            handleResponseBody(
                cv,
                response = it,
                adapter = HttpRequestHeaderBodyJsonMapAdapter.newBuilder()
                    .addAllExpressions(listOf("globalVarOf\$exp_test"))
                    .build()
                    .pack_()
            )
        }
        // content-type: text/plain; charset=utf-8
        Assert.assertEquals(listOf("text/plain; charset=utf-8"), res)
    }

    @Test
    fun testJsonAdapter2() {
        val res = httpCaller.callUrl(
            url = "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/tag.json",
            method = "GET",
            headers = emptyMap(),
            requestBody = null,
        ).let {
            handleResponseBody(
                RuleContextAndVars(
                    contextData = ContextData.obtain(),
                    globalVarsLazy = { emptyList() },
                    localVars = LocalVars.obtain(),
                ),
                response = it,
                adapter = HttpRequestJsonMapAdapter.newBuilder()
                    .addAllExpressions(listOf("colors.red"))
                    .build()
                    .pack_()
            )
        }
        Assert.assertEquals(listOf("#FF5252"), res)
    }

    @Test
    fun testJsonAdapterWithHeaderAndBody2() {
        val res = httpCaller.callUrl(
            url = "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/tag.json",
            method = "GET",
            headers = emptyMap(),
            requestBody = null,
        ).let {
            handleResponseBody(
                RuleContextAndVars(
                    contextData = ContextData.obtain(),
                    globalVarsLazy = { emptyList() },
                    localVars = LocalVars.obtain(),
                ),
                response = it,
                adapter = HttpRequestHeaderBodyJsonMapAdapter.newBuilder()
                    .addAllExpressions(listOf("body.colors.red"))
                    .build()
                    .pack_()
            )
        }
        Assert.assertEquals(listOf("#FF5252"), res)
    }

    @Test
    fun testRawAdapter() {
        val res = httpCaller.callUrl(
            url = "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/tag.json",
            method = "GET",
            headers = emptyMap(),
            requestBody = null,
        ).let {
            handleResponseBody(
                RuleContextAndVars(
                    contextData = ContextData.obtain(),
                    globalVarsLazy = { emptyList() },
                    localVars = LocalVars.obtain(),
                ),
                response = it,
                adapter = HttpRequestRawAdapter.newBuilder().build().pack_()
            )
        }
        Assert.assertEquals(
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
            ),
            res,
        )
    }

    @Test
    fun testRequestBody() {
        val res = httpCaller.callUrl(
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
                .toHttpRequestBody(
                    RuleContextAndVars(
                        contextData = ContextData.obtain(),
                        localVars = LocalVars.obtain()
                    ) { emptyList() })
        ).let {
            handleResponseBody(
                RuleContextAndVars(
                    contextData = ContextData.obtain(),
                    globalVarsLazy = { emptyList() },
                    localVars = LocalVars.obtain(),
                ),
                response = it,
                adapter = HttpRequestRawAdapter.newBuilder().build().pack_()
            )
        }

        println(res)
    }

    @Test
    fun testFixExpressions() {
        Assert.assertEquals("name_1", "name-1".fixSpecialLetters())
        Assert.assertEquals("name__1", "name--1".fixSpecialLetters())
        Assert.assertEquals("name__1_2", "name--1-2".fixSpecialLetters())
        Assert.assertEquals("name_1", "name 1".fixSpecialLetters())
        Assert.assertEquals("name__1", "name  1".fixSpecialLetters())
    }
}