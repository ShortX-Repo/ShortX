package tornaco.apps.shortx.ui.base

typealias I18NKey = String

interface I18N {
    operator fun get(key: String, args: Map<String, String> = emptyMap()): String
}