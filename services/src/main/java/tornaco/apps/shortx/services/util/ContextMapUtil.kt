package tornaco.apps.shortx.services.util

import tornaco.apps.shortx.services.rule.ContextData

object ContextMapUtil {
    fun String.inflateContextMap(contextData: ContextData): String {
        var res = this
        if (contextData.isEmpty()) {
            return res
        }
        contextData.forEach {
            res = res.replace("\$${it.first}", "${it.second}", false)
        }
        return res
    }
}