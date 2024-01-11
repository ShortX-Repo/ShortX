package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.rule.ProtoMessage

internal abstract class InternalFactListener(val usage: String) {
    abstract suspend fun onFact(
        fact: ProtoMessage,
        initialContextDataLazy: () -> Map<String, Any>
    )
}