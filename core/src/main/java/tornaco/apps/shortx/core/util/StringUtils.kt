package tornaco.apps.shortx.core.util

fun String?.fallbackOnEmpty(string: String): String {
    return if (this.isNullOrEmpty()) string else this
}