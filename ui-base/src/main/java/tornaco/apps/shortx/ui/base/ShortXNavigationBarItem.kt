package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.ShortXNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    alwaysShowLabel: Boolean = true,
) {
    val selectedContentColor = MaterialTheme.colorScheme.primary
    val unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.88f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = ShortXCardRoundedCornerShapeXL)
                .background(color = containerColor)
                .defaultMinSize(minHeight = 36.dp)
                .clickable(
                    role = Role.Tab,
                    onClick = onClick,
                )
                .padding(PaddingValues(horizontal = 10.dp, vertical = 4.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (selected) selectedContentColor else unselectedContentColor,
            ) {
                icon()
                if (alwaysShowLabel || selected) {
                    Box(Modifier.offset(y = (-4).dp)) {
                        label()
                    }
                }
            }
        }
    }
}
