package tornaco.apps.shortx.core.util

import android.os.Bundle
import tornaco.apps.shortx.core.proto.common.AppPkg

fun Bundle.bundleToString(): String {
    return when {
        this.isEmpty -> "EMPTY BUNDLE"
        else -> this.keySet().joinToString(", ") {
            "$it-${get(it)}"
        }
    }
}

fun Map<*, *>.mapToString(separator: CharSequence = ", "): String {
    return when {
        this.isEmpty() -> "EMPTY BUNDLE"
        else -> this.keys.joinToString(separator) {
            "$it-${get(it)}"
        }
    }
}

fun AppPkg.appPkgToString(): String {
    return "$pkgName-u$userId"
}