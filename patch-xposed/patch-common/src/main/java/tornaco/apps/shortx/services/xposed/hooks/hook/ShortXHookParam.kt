package tornaco.apps.shortx.services.xposed.hooks.hook

import java.lang.reflect.Member
import java.lang.reflect.Method

class ShortXHookParam(
    val method: Member,
    val thisObject: Any?,
    val args: Array<Any?>,
    result: Any? = null,
    throwable: Throwable? = null,
    val isBefore: Boolean = false,
) {
    var result: Any? = result
        set(value) {
            field = value
            resultChanged = true
        }

    var throwable: Throwable? = throwable
        set(value) {
            field = value
            throwableChanged = true
        }

    var resultChanged: Boolean = false
        private set

    var throwableChanged: Boolean = false
        private set

    var skipOriginal: Boolean = false
        private set

    val methodOrNull: Method?
        get() = method as? Method

    fun returnAndSkip(value: Any?) {
        result = value
        skipOriginal = true
    }

    fun throwAndSkip(value: Throwable) {
        throwable = value
        skipOriginal = true
    }
}
