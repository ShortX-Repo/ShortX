package tornaco.apps.shortx.services.rule.actor

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Test

class CoroutineTest {

    @Test
    fun testTimeout() {
        runBlocking {
            val res = withTimeoutOrNull(100) {
                Thread.sleep(88)
                "Hello"
            }

            println(res)
        }

        runBlocking {
            val res = withTimeoutOrNull(100) {
                Thread.sleep(111)
                "Hello"
            }

            println(res)
        }
    }
}