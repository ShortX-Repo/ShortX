package tornaco.apps.shortx.services.util

import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import org.junit.Test

class TimeoutTest {
    @Test
    fun should_wait_util_timeout() {
        runBlocking {
            waitUntil(3000, 100) {
                false
            } shouldBe false
        }
    }

    @Test
    fun should_wait_util_match() {
        val start = System.currentTimeMillis()
        runBlocking {
            waitUntil(3000, 100) {
                it > 10
            }
        } shouldBe true
        val taken = System.currentTimeMillis() - start
        println("Taken: $taken ms")
    }
}