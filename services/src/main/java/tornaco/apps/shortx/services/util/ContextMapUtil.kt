package tornaco.apps.shortx.services.util

object ContextMapUtil {
    fun String.inflateContextMap(contextMap: Map<String, Any> = emptyMap()): String {
        var res = this
        if (contextMap.isEmpty()) {
            return res
        }
        contextMap.forEach {
            res = res.replace("\$${it.key}", it.value.toString(), false)
        }
        return res
    }
}