package tornaco.apps.shortx.core.util

import com.google.gson.JsonSyntaxException
import io.kotest.assertions.throwables.shouldThrow
import org.junit.Test

class JsonUtilsTest {
    @Test
    fun testFormatJson() {
        println(formatJsonString("""{"name": "John", "age": 30, "city": "New York"}"""))
    }

    @Test
    fun testFormatNonJson() {
        shouldThrow<JsonSyntaxException> {
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