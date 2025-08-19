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
            val value = it.value
            // Support index, e.g. {list}[0]
            if (value is List<*>) {
                value.forEachIndexed { index, listItem ->
                    // {list}[0]
                    stringRes = stringRes.replace(
                        "${it.key.asContextDataKey()}[$index]",
                        listItem.contextDataValueToString()
                    )
                    // {list}.get(0)
                    stringRes = stringRes.replace(
                        "${it.key.asContextDataKey()}.get($index)",
                        listItem.contextDataValueToString()
                    )
                }
            }

            stringRes =
                stringRes.replace(it.key.asContextDataKey(), value.contextDataValueToString())
        }
        return stringRes
    }

    fun Any?.contextDataValueToString(): String {
        if (this is FuncReturnValue) return this.value
        if (this is List<*>) return this.joinToString()
        return this?.toString().fallbackOnEmpty("")
    }
}