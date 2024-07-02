package tornaco.apps.shortx.services.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

suspend fun CoroutineScope.loop() {
    while (isActive) delay(100000)
}