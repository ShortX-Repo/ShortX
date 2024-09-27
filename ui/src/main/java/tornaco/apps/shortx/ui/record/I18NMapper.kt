package tornaco.apps.shortx.ui.record

import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import java.util.Locale

fun ContextSrcEvent.displayLabel(i18N: I18N): String {
    return i18N["ui.settings.action.record.src.${this.name.lowercase(Locale.ENGLISH)}"]
}