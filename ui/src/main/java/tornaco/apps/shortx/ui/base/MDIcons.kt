package tornaco.apps.shortx.ui.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

object MDIcons {
    fun filled(name: String): ImageVector = MDIconsFilled.all[name] ?: Icons.Filled.Settings
    fun filled(): Map<String, ImageVector> = MDIconsFilled.all

    fun outlined(name: String): ImageVector = MDIconsOutlined.all[name] ?: Icons.Outlined.Settings
    fun outlined(): Map<String, ImageVector> = MDIconsOutlined.all
}