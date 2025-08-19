package tornaco.apps.shortx.core

import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.context.SystemEnv
import tornaco.apps.shortx.core.context.SystemEnv.inflateSystemEnv
import tornaco.apps.shortx.core.util.logAdapter

class SystemEnvTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testInflateSystemEnv() {
        // 定义变量映射
        val variables: Map<String, (String?) -> String?> = mapOf(
            "currentTime" to { _ -> "2011" },
            "username" to { _ -> "John Doe" },
            "randomNumber" to { _ -> "111111" },
            // 带参数的变量：获取系统设置值
            "systemSettings" to { key ->
                key?.let {
                    "$key-VALUE"
                } ?: "No Key Provided"
            }
        )
        variables.forEach { (s, function) ->
            SystemEnv.addEnvInflater(s to function)
        }

        val template =
            "Current time is %currentTime%, user is %username%, random number: %randomNumber%, %systemSettings@screen_brightness%"
        val result = template.inflateSystemEnv()

        result shouldBe "Current time is 2011, user is John Doe, random number: 111111, screen_brightness-VALUE"
    }

}