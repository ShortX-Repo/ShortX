package tornaco.apps.shortx.ui.base

interface I18N {
    operator fun get(key: String, args: Map<String, String> = emptyMap()): String
}