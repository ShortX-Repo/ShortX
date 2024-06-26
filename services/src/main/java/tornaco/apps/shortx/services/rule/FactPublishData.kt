package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.rule.ProtoMessage

data class FactPublishData<T : ProtoMessage>(
    val reason: String,
    val fact: T,
    val initialContextDataLazy: () -> Map<String, Any> = { emptyMap() }
)
