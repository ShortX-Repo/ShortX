package tornaco.apps.shortx.services.rule.actor

import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.util.logAdapter

class RegexTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testRegxt() {
        println(".*/Downloads/.*".toRegex().matches("/sdcard/Downloads/A.mp3"))
    }
}
