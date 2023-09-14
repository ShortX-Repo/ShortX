package tornaco.apps.ui

import io.kotest.matchers.shouldBe
import org.junit.Test
import tornaco.apps.shortx.ui.addrule.action.selector.http.toHttpCanaryExportJson

class HttpCanaryExportJsonTest {
    @Test
    fun testParse() {
        val json = """
            {
              "app": "com.mihoyo.hyperion",
              "duration": "1127ms",
              "headers": {
                "DS": "1694515611,gci0jl,55401fb220990a39a712201af4a7bf77",
                "cookie": "stuid\u003d272229931;stoken\u003dv2_rA7PzTweKLvrb-FaBwiNERCF70mxGN_GMrdQ8LVKNg9thu8cnduQqOhS5WbPNjeNzw7M6bfHAwbqSeKFPfjvBHCV8WUTKpxv72-OBg\u003d\u003d;mid\u003d0uag3j1yzg_mhy;",
                "x-rpc-client_type": "2",
                "x-rpc-app_version": "2.52.1",
                "x-rpc-sys_version": "13",
                "x-rpc-channel": "miyousheluodi",
                "x-rpc-device_id": "2f52ab7d-1757-3561-9ed8-e4fb375688d9",
                "x-rpc-device_fp": "38d7f015c16c1",
                "x-rpc-device_name": "OnePlus PHK110",
                "x-rpc-device_model": "PHK110",
                "Referer": "https://app.mihoyo.com",
                "x-rpc-verify_key": "bll8iq97cem8",
                "Content-Length": "0",
                "Host": "bbs-api.miyoushe.com",
                "Connection": "Keep-Alive",
                "Accept-Encoding": "gzip",
                "User-Agent": "okhttp/4.9.3"
              },
              "method": "POST",
              "protocol": "HTTP/1.1",
              "remoteIp": "203.107.60.77",
              "remotePort": 443,
              "sessionId": "49d3f565-b699-47c9-9207-f55543049462",
              "time": "2023-09-12 18:46:51",
              "url": "https://bbs-api.miyoushe.com/vila/api/villa/own/member/roles"
            }
        """.trimIndent()


        val request = json.toHttpCanaryExportJson()
        request.url shouldBe "https://bbs-api.miyoushe.com/vila/api/villa/own/member/roles"
        request.method shouldBe "POST"
        request.headers["User-Agent"] shouldBe "okhttp/4.9.3"
        request.headers["x-rpc-device_model"] shouldBe "PHK110"
        request.headers["cookie"] shouldBe "stuid=272229931;stoken=v2_rA7PzTweKLvrb-FaBwiNERCF70mxGN_GMrdQ8LVKNg9thu8cnduQqOhS5WbPNjeNzw7M6bfHAwbqSeKFPfjvBHCV8WUTKpxv72-OBg==;mid=0uag3j1yzg_mhy;"
    }
}