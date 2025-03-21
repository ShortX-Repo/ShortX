package tornaco.apps.shortx.services.rule.actor

import org.junit.Test

class ktTest {

    @Test
    fun testMapKeys() {
        val map1 = mapOf("a" to 12, "b" to 29)
        val mapped = map1.mapKeys {
            it.key + "-"
        }
        println(map1)
        println(mapped)
    }
}