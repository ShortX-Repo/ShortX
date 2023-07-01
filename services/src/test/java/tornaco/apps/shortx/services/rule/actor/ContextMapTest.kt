package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Test
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap

class ContextMapTest {

    @Test
    fun testInflateMap() {
        Assert.assertEquals(
            "if (This is Hello..contains(\"Hello\")) { System.err.println(name) }",
            "if ({contentText}.contains(\"Hello\")) { System.err.println(name) }".inflateContextMap(
                mapOf("contentText" to "This is Hello.")
            )
        )

        Assert.assertEquals(
            "if (\"This is Hello.\".contains(\"Hello\")) { System.err.println(name) }",
            "if ({contentText}.contains(\"Hello\")) { System.err.println(name) }".inflateContextMap(
                mapOf("contentText" to "\"This is Hello.\"")
            )
        )
    }
}