package tornaco.apps.shortx.core

typealias I18NKey = String

interface I18N {
    operator fun get(key: String, args: Map<String, String> = emptyMap(), fallback: String? = null): String
}