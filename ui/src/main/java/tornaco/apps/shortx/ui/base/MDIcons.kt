package tornaco.apps.shortx.ui.base

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

object MDIcons {
    fun filled(name: String): ImageVector = MDIconsFilled.all[name] ?: Icons.Filled.Settings
    fun filled(): Map<String, ImageVector> = MDIconsFilled.all
}