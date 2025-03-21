package tornaco.apps.shortx.services.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

suspend fun CoroutineScope.loopUntilQuitWithError(quit: () -> Throwable? = { null }) {
    while (isActive) {
        val err = quit()
        if (err != null) throw err
        delay(1000)
    }
}