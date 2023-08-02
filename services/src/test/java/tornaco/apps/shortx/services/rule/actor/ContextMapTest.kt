package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.context.allKeys
import tornaco.apps.shortx.core.util.logAdapter
import kotlin.reflect.KClass

class ContextMapTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

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

    @Test
    fun testAllKeys() {
        fun getEnumValues(enumClass: KClass<out Enum<*>>): Array<out Enum<*>> =
            enumClass.java.enumConstants

        val expected = ContextDataMapping::class.nestedClasses.map { enumClazz ->
            @Suppress("UNCHECKED_CAST")
            getEnumValues(enumClazz as KClass<out Enum<*>>).map { it.name }
        }.flatten().distinct().sorted()

        val actual = allKeys

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testContextDataObtain() {
        val obtained = ContextData.obtain()
        Assert.assertTrue(obtained.map.keys.containsAll(allKeys))
        println("allKeys: ${allKeys.size}")

        val obtainedWithInit = ContextData.obtain(
            mapOf(
                "testKey1" to "value1",
                ContextDataMapping.Context.contextSrcId.name to "valueOverride"
            )
        )
        Assert.assertTrue(obtained.map.keys.containsAll(allKeys))
        Assert.assertEquals("value1", obtainedWithInit.get("testKey1"))
        Assert.assertEquals("valueOverride", obtainedWithInit.get("contextSrcId"))
        Assert.assertEquals("", obtainedWithInit.get("contentText"))

        obtainedWithInit.recycle()

        val obtainedReuse = ContextData.obtain()
        Assert.assertTrue(obtainedReuse.map.keys.containsAll(allKeys))
    }

    @Test
    fun testPop() {
        val data = ContextData.obtain()
        data.add("name", "Nick")
        Assert.assertEquals("Nick", data.pop("name"))
        Assert.assertNull(data.get("Nick"))
    }
}