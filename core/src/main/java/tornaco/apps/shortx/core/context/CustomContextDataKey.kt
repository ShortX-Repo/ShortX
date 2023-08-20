package tornaco.apps.shortx.core.context

import tornaco.apps.shortx.core.proto.common.CustomContextDataKey

fun CustomContextDataKey.forKey(key: String): String? {
    return keysList.firstOrNull { it.first == key }?.second
}