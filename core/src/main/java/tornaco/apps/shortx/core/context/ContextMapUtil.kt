package tornaco.apps.shortx.core.context

import android.icu.text.MessageFormat

object ContextMapUtil {
    fun String.inflateContextMap(contextData: ContextData): String {
        return inflateContextMap(contextData.map)
    }

    fun String.inflateContextMap(contextData: Map<String, Any?>): String {
        // FAST Check if empty.
        if (contextData.isEmpty()) return this
        return MessageFormat(this).format(contextData)
    }
}