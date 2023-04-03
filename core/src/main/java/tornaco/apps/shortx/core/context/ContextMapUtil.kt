package tornaco.apps.shortx.core.context

import android.icu.text.MessageFormat

object ContextMapUtil {
    fun String.inflateContextMap(contextData: ContextData): String {
        return MessageFormat(this).format(contextData.map)
    }
}