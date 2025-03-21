package tornaco.apps.shortx.services.rule.actor

import org.junit.Test

class FlagTest {

    @Test
    fun testFlags() {
        // 1711276032
        println(0x00000001)
        println(0x00000002)
        println(0x01000000)
        println(0x02000000)
        println(0x04000000)
        println(0x20000000)
        println(0x40000000)
        println("-------")

        println(0x00000002 or 0x01000000)
        println(0x20000000 or 0x40000000 or 0x04000000 or 0x02000000)
    }
}