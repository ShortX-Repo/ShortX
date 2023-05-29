package tornaco.apps.shortx.core.context

import android.icu.text.MessageFormat

object ContextMapUtil {
    fun String.inflateContextMap(contextData: ContextData): String {
        // FAST Check if empty.
        if (contextData.map.isEmpty()) return this
        return MessageFormat(this).format(contextData.map)
    }
}