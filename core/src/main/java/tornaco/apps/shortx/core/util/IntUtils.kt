package tornaco.apps.shortx.core.util

inline fun Int.times(action: (Int) -> Unit) {
    repeat(this, action)
}