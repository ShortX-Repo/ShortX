package tornaco.apps.shortx.services.rule.actor

import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.HttpRequestAdapter
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.rule.actor.impl.HttpRequest

class HttpRequestTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testReq() {
        HttpRequest.handleUrl(
            //"https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/tag.json",
            "https://raw.githubusercontent.com/Tornaco/Thanox/master/files/profile/profiles/forceidle.json",
            HttpRequestAdapter.Json
        )
    }
}