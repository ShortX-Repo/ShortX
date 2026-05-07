package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RowScope.ShortXNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        modifier = Modifier,
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = label,
        enabled = alwaysShowLabel
    )
}
