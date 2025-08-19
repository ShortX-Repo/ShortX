package tornaco.apps.shortx.core.context

fun String.asContextDataKey(): String {
    return "{${this}}"
}