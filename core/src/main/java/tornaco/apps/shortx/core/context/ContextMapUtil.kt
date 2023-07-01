package tornaco.apps.shortx.core.context

object ContextMapUtil {
    fun String.inflateContextMap(contextData: ContextData): String {
        return inflateContextMap(contextData.map)
    }

    fun String.inflateContextMap(contextData: Map<String, Any?>): String {
        // FAST Check if empty.
        if (contextData.isEmpty()) return this

        var stringRes = this
        contextData.forEach {
            stringRes = stringRes.replace("{${it.key}}", it.value.toString())
        }
        return stringRes
    }
}