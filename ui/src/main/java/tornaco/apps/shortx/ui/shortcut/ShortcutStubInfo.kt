package tornaco.apps.shortx.ui.shortcut

import androidx.annotation.ColorInt

data class ShortcutStubInfo(
    val appLabel: String,
    val appPkgName: String,
    val appIcon: String,
    @ColorInt
    val appIconTintColor: Int,
    @ColorInt
    val appIconBgColor: Int,

    // Target DA
    val daId: String? = null
)
