package tornaco.apps.shortx.services.rule.actor

import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.util.logAdapter
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegexTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testRegex() {
        println(".*/Downloads/.*".toRegex().matches("/sdcard/Downloads/A.mp3"))
    }

    @Test
    fun testRegex2() {
        // println(".*HELLO.*".toRegex().matches("HELLO world."))

        // val pattern = Pattern.compile("\\b\\d{4,10}\\b")
        val pattern = Pattern.compile("\\d{4,10}")
        val matcher = pattern.matcher("This is a test HELLO WORLD123456, qi")
        if (matcher.find()) {
            println("Found")
            val matched = matcher.group()
            println(matched)
        } else {
            println("NOT FOUND")
        }

    }
}
