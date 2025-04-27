package tornaco.apps.shortx.services.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

suspend fun waitUntil(
    timeoutMillis: Long,
    checkIntervalMillis: Long = 50,
    condition: (checkTime: Int) -> Boolean
): Boolean {
    return runCatching {
        var checkTime = 0
        withTimeout(timeoutMillis) {
            while (!condition(checkTime++)) {
                delay(checkIntervalMillis)
            }
        }
        true
    }.getOrElse {
        false
    }
}
