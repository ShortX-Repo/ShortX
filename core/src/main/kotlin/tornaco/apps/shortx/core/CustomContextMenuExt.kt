package tornaco.apps.shortx.core

import tornaco.apps.shortx.core.proto.common.CustomContextMenu

fun CustomContextMenu.toActivityClassName(): String {
    return BuildPropInternal.CUSTOM_CONTEXT_MENU_CLASSNAME_BASE + (number + 1).toString()
}

fun String.classNameToCustomContextMenu(): CustomContextMenu? {
    return kotlin.runCatching { substringAfter(BuildPropInternal.CUSTOM_CONTEXT_MENU_CLASSNAME_BASE).toIntOrNull() }
        .getOrNull()?.let {
            CustomContextMenu.forNumber(it - 1)
        }
}