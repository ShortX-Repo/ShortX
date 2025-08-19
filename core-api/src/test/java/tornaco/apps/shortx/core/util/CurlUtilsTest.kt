package tornaco.apps.shortx.core.util

import io.kotest.matchers.shouldBe
import lang3.tuple.Pair
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.util.curl.BasicCurlParser

class CurlUtilsTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun parseCurlWithApache() {
        val curlCommand =
            "curl \"https://example.com\" -X POST -H \"Content-Type: application/json\" --data \"{'key': 'value'}\""

        val parser = BasicCurlParser()
        val request = parser.parse(curlCommand)

        request.url shouldBe "https://example.com"
        request.method shouldBe "POST"
        request.headers shouldBe listOf(
            Pair.of("Content-Type", "application/json")
        )
        request.postData shouldBe "{'key': 'value'}"

        println(request.toString())
    }

    @Test
    fun parseCurlWithApache2() {
        val curlCommand =
            "curl -L \\\n" +
                    "  -H \"Accept: application/vnd.github+json\" \\\n" +
                    "  -H \"Authorization: Bearer <YOUR-TOKEN>\" \\\n" +
                    "  -H \"X-GitHub-Api-Version: 2022-11-28\" \\\n" +
                    "  https://api.github.com/repos/OWNER/REPO/collaborators"

        val parser = BasicCurlParser()
        val request = parser.parse(curlCommand)
        println(request.toString())
    }

    @Test
    fun parseCurlWithApache3() {
        val curlCommand =
            """
                curl -L \
  -X POST \
  -H "Accept: application/vnd.github+json" \
  -H "Authorization: Bearer <YOUR-TOKEN>" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  https://api.github.com/repos/OWNER/REPO/check-runs \
  -d '{"name":"mighty_readme","head_sha":"ce587453ced02b1526dfb4cb910479d431683101","status":"in_progress","external_id":"42","started_at":"2018-05-04T01:14:52Z","output":{"title":"Mighty Readme report","summary":"","text":""}}'
            """.trimIndent()

        val parser = BasicCurlParser()
        val request = parser.parse(curlCommand)
        println(request.toString())
    }


    @Test
    fun parseCurlWithApache4() {
        val curlCommand = "                 curl -X POST https://reqbin.com/echo/post/form\n" +
                "   -H \"Content-Type: application/x-www-form-urlencoded\" \n" +
                "   -d \"param1=value1&param2=value2\" "

        val parser = BasicCurlParser()
        val request = parser.parse(curlCommand)
        println(request.toString())
    }


    @Test
    fun parseCurlWithApache5() {
        val curlCommand =
            "curl -X POST -F 'username=davidwalsh' -F 'password=something' http://domain.tld/post-to-me.php"

        val parser = BasicCurlParser()
        val request = parser.parse(curlCommand)
        println(request.toString())
    }


 @Test
    fun parseCurlWithApache6() {
        val curlCommand =
            "curl -X POST -F 'image=@/path/to/pictures/picture.jpg' http://domain.tld/upload"

        val parser = BasicCurlParser()
        val request = parser.parse(curlCommand)
        println(request.toString())
    }

 @Test
    fun parseCurlWithApache7() {
        val curlCommand =
            "curl -X POST -H \"x-version:2022-11-08\" -H \"user-agent:Dart/2.17 (dart:io)\" -H \"appid:4150439554430523\" -H \"ts:1695715212296\" -H \"accept-encoding:gzip\" -H \"content-length:0\" -H \"authentication:WelGhv/lRa+j2eVSbbFkozDSo2o2Fyy/5KKGIzjS8Z1KbqLG+XxaOhEObJjpGQdwJ3QmwGMapkh1t71zZqXCZQ==\" -H \"host:116.205.235.27:8090\" -H \"content-type:application/json; charset=utf-8\" -H \"x-token:xjfjnrjcisjsnfnfr\" -d \"\" \"http://116.205.235.27:8090/app/task/sign\""

        val parser = BasicCurlParser()
        val request = parser.parse(curlCommand)
        println(request.toString())
    }

}