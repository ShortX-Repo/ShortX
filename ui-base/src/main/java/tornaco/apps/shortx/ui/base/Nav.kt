package tornaco.apps.shortx.ui.base

import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

fun NavController.popBackWithResult(data: Map<String, Any>) {
    this.previousBackStackEntry?.savedStateHandle?.let {
        for ((key, value) in data) {
            it[key] = value
        }
    }
    popBackStack()
}

fun <T> NavController.getStateFlow(key: String, initialValue: T): StateFlow<T>? {
    return this.currentBackStackEntry?.savedStateHandle?.getStateFlow(key, initialValue)
}

fun <T> NavController.removeData(key: String): T? {
    return this.currentBackStackEntry?.savedStateHandle?.remove(key)
}

suspend fun afterNavDelay() {
    delay(NAV_ANIM_DURATION + 20L)
}

suspend fun afterNavDelay(loadingTime: Long) {
    val delay = maxOf(NAV_ANIM_DURATION + 20L - loadingTime, 0L)
    delay(delay)
}

suspend fun afterNavLongDelay() {
    delay(NAV_ANIM_DURATION + 120L)
}