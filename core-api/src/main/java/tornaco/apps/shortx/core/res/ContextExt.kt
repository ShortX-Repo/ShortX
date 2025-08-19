package tornaco.apps.shortx.core.res

import android.content.Context
import tornaco.apps.shortx.core.res.Remix.allFlat

fun Context.remixResId(remixName: String, size: Int = 24): Int {
    val name = allFlat[remixName]?.let {
        if (size == 24) it else "${it}_${size}dp"
    }
    return resources.getIdentifier(
        name ?: allFlat[Remix.Development.bug_line],
        "drawable",
        packageName
    )
}

fun remixResName(remixName: String): String? {
    return allFlat[remixName]
}

fun String.isBase64Image(): Boolean {
    return length > 100 || remixResName(this) == null
}