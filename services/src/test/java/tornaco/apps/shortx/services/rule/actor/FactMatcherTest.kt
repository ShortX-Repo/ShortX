package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.RegexMatchData
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
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
            RegexMatchData(".*", RegexMatchOptions.RegexMatchOptions_Match) to "ABC",
            RegexMatchData("", RegexMatchOptions.RegexMatchOptions_Match) to "Hello",
            RegexMatchData("""\d+""", RegexMatchOptions.RegexMatchOptions_Match) to "123"
        )
        Assert.assertTrue(res)
    }

    @Test
    fun testEmptyOrRegextNotMatch() {
        val res = emptyOrRegexMatch(
            RegexMatchData(".*", RegexMatchOptions.RegexMatchOptions_Match) to "ABC",
            RegexMatchData("", RegexMatchOptions.RegexMatchOptions_Match) to "Hello",
            RegexMatchData("""\d+""", RegexMatchOptions.RegexMatchOptions_Match) to "123a"
        )
        Assert.assertFalse(res)
    }
}