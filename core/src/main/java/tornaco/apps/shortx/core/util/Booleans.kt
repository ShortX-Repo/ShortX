package tornaco.apps.shortx.core.util

fun <T> Boolean.select(t: T, f: T): T = if (this) t else f
inline fun <T> Boolean.select(t: () -> T, f: () -> T): T = if (this) t() else f()