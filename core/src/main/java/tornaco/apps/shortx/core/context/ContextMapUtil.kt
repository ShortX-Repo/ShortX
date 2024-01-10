package tornaco.apps.shortx.core.context

import tornaco.apps.shortx.core.proto.func.FuncReturnValue
import tornaco.apps.shortx.core.util.fallbackOnEmpty

object ContextMapUtil {
    fun String.inflateContextMap(contextData: ContextData): String {
        return inflateContextMap(contextData.map)
    }

    fun String.inflateContextMap(contextData: Map<String, Any?>): String {
        // FAST Check if empty.
        if (contextData.isEmpty()) return this

        var stringRes = this
        contextData.forEach {
            stringRes = stringRes.replace("{${it.key}}", it.value.contextDataValueToString())
        }
        return stringRes
    }

    private fun Any?.contextDataValueToString(): String {
        if (this is FuncReturnValue) return this.value
        return this?.toString().fallbackOnEmpty("")
    }
}