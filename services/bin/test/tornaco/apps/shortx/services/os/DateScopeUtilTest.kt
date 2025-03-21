package tornaco.apps.shortx.services.os

import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.condition.TheXXTimeTodayScope
import tornaco.apps.shortx.core.util.logAdapter

class DateScopeUtilTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testScopes() {
        TheXXTimeTodayScope.entries.forEach {
            println("$it --> ${formatTheXXTimeTodayScope(it)}")
        }
    }
}