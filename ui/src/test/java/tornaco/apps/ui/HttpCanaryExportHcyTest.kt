package tornaco.apps.ui

import android.webkit.URLUtil
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Test
import tornaco.apps.shortx.ui.addrule.action.selector.http.toHttpCanaryExportHcy

class HttpCanaryExportHcyTest {
    @Test
    fun testParse() {
        val hcy = """
            POST /binding/api/genAuthKey HTTP/1.1
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
        """.trimIndent()

        mockkStatic(URLUtil::class) {
            every {
                URLUtil.isNetworkUrl(any())
            } returns false

            val req = hcy.toHttpCanaryExportHcy()
            println(req)

            req.host shouldBe "https://api-takumi.miyoushe.com"
            req.path shouldBe "/binding/api/genAuthKey"
            req.url shouldBe "https://api-takumi.miyoushe.com/binding/api/genAuthKey"
            req.method shouldBe "POST"
            req.body shouldBe """{"auth_appid":"im_ccs","game_biz":"hk4e_cn","game_uid":181588968,"region":"cn_gf01"}"""
            req.headers.size shouldBe 18
            req.headerAsMap["User-Agent"] shouldBe "okhttp/4.9.3"
            req.headerAsMap["x-rpc-device_model"] shouldBe "PHK110"
            req.headerAsMap["cookie"] shouldBe "stuid=282679178;stoken=v2_nCRrKqGvIUgdOG8PC_5fmr9qXi7MiEIT7N30B3PJ-HDbZQ3uADBo1JeUuXNUgTOAht3F81eZ2l_PThnl6qwwS4qi4Nw=;mid=0c3pm41yyr_mhy;"
        }

    }

    @Test
    fun testParseNoBody() {
        val hcy = """
            POST /vila/api/villa/own/member/roles HTTP/1.1
            DS: 1694515611,gci0jl,55401fb220990a39a712201af4a7bf77
            cookie: stuid=272229931;stoken=v2_rA7PzTweKLvrb-FaBwiNERCF70mxGN_GMrdQ8LVKNg9thu8cnduQqOhS5WbPNjeNzw7M6bfHAwbqSeKFPfjvBHCV8WUTKpxv72-OBg==;mid=0uag3j1yzg_mhy;
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
            Content-Length: 0
            Host: bbs-api.miyoushe.com
            Connection: Keep-Alive
            Accept-Encoding: gzip
            User-Agent: okhttp/4.9.3


        """.trimIndent()

        mockkStatic(URLUtil::class) {
            every {
                URLUtil.isNetworkUrl(any())
            } returns false

            val req = hcy.toHttpCanaryExportHcy()
            println(req)

            req.host shouldBe "https://bbs-api.miyoushe.com"
            req.path shouldBe "/vila/api/villa/own/member/roles"
            req.url shouldBe "https://bbs-api.miyoushe.com/vila/api/villa/own/member/roles"
            req.method shouldBe "POST"
            req.body shouldBe null
            req.headers.size shouldBe 17
            req.headerAsMap["User-Agent"] shouldBe "okhttp/4.9.3"
            req.headerAsMap["x-rpc-device_model"] shouldBe "PHK110"
            req.headerAsMap["cookie"] shouldBe "stuid=272229931;stoken=v2_rA7PzTweKLvrb-FaBwiNERCF70mxGN_GMrdQ8LVKNg9thu8cnduQqOhS5WbPNjeNzw7M6bfHAwbqSeKFPfjvBHCV8WUTKpxv72-OBg==;mid=0uag3j1yzg_mhy;"
        }

    }
}