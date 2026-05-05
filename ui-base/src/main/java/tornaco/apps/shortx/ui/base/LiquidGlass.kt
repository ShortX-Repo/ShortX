package tornaco.apps.shortx.ui.base

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

@Composable
fun isLiquidGlassEnabled(): Boolean {
    return LocalUiSettings.current.uiExperimental && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

@Composable
fun rememberShortXGlassBackdrop(baseColor: Color): Backdrop {
    return rememberCanvasBackdrop {
        drawRect(baseColor)
    }
}

@Composable
fun ShortXLiquidGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit,
) {
    val backdrop = rememberShortXGlassBackdrop(MaterialTheme.colorScheme.surface)
    val resolvedSurfaceColor =
        if (surfaceColor.isSpecified) surfaceColor else shortXGlassSurfaceColor()
    Row(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(24.dp) },
                effects = {
                    vibrancy()
                    blur(10.dp.toPx())
                    lens(14.dp.toPx(), 20.dp.toPx())
                },
                onDrawSurface = {
                    if (tint.isSpecified) {
                        drawRect(tint.copy(alpha = 0.22f))
                    }
                    drawRect(resolvedSurfaceColor)
                }
            )
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .defaultMinSize(minHeight = 48.dp)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
fun RowScope.ShortXNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    alwaysShowLabel: Boolean = true,
) {
    if (!isLiquidGlassEnabled()) {
        NavigationBarItem(
            selected = selected,
            onClick = onClick,
            icon = icon,
            label = label,
            alwaysShowLabel = alwaysShowLabel,
        )
        return
    }

    val selectedContentColor = MaterialTheme.colorScheme.primary
    val unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    val itemTint = if (selected) selectedContentColor else Color.Unspecified
    val contentColor = if (selected) selectedContentColor else unselectedContentColor
    ShortXLiquidGlassButton(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        tint = itemTint,
        surfaceColor = shortXGlassSurfaceColor(alpha = if (selected) 0.28f else 0.16f),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                icon()
            }
        }
    }
}

@Composable
fun ShortXLiquidGlassNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val backdrop = rememberShortXGlassBackdrop(MaterialTheme.colorScheme.surface)
    val surfaceColor = shortXGlassSurfaceColor(alpha = 0.24f)
    Row(
        modifier = modifier
            .drawBackdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(30.dp) },
                effects = {
                    vibrancy()
                    blur(14.dp.toPx())
                    lens(18.dp.toPx(), 24.dp.toPx())
                },
                onDrawSurface = {
                    drawRect(surfaceColor)
                }
            )
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
fun shortXGlassSurfaceColor(alpha: Float = 0.22f): Color {
    val isDarkTheme = isSystemInDarkTheme()
    return remember(alpha, isDarkTheme) {
        if (isDarkTheme) {
            Color(0xFF15171A).copy(alpha = alpha + 0.06f)
        } else {
            Color.White.copy(alpha = alpha)
        }
    }
}