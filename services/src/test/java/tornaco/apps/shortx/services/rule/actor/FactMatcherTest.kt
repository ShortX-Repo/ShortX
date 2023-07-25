package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.rule.emptyOrRegexMatch

class FactMatcherTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testEmptyOrRegextMatch() {
        val res = emptyOrRegexMatch(
            ".*" to "ABC",
            "" to "Hello",
            """\d+""" to "123"
        )
        Assert.assertTrue(res)
    }

    @Test
    fun testEmptyOrRegextNotMatch() {
        val res = emptyOrRegexMatch(
            ".*" to "ABC",
            "" to "Hello",
            """\d+""" to "123a"
        )
        Assert.assertFalse(res)
    }
}