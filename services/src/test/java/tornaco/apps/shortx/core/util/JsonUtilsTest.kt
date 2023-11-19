package tornaco.apps.shortx.core.util

import io.kotest.assertions.shouldFail
import org.junit.Test

class JsonUtilsTest {
    @Test
    fun testFormatJson() {
        println(formatJsonString("""{"name": "John", "age": 30, "city": "New York"}"""))
    }

    @Test
    fun testFormatNonJson() {
        shouldFail {
            println(
                formatJsonString(
                    """Hello, nice to meet you,
                   are you ok?
        """.trimMargin()
                )
            )
        }
    }
}