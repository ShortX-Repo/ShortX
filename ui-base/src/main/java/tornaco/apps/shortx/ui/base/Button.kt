@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun DropdownButtonLayout(
    text: String,
    dropState: DropdownSelectorState,
    modifier: Modifier = Modifier
) {
    SplitButtonLayout(
        modifier = modifier,
        leadingButton = {
            SplitButtonDefaults.TonalLeadingButton(
                onClick = { /* Do Nothing */ },
            ) {
                Text(text)
            }
        },
        trailingButton = {
            SplitButtonDefaults.TonalTrailingButton(
                checked = dropState.isMenuOpen,
                onCheckedChange = {
                    dropState.open()
                },
            ) {
                val rotation: Float by
                animateFloatAsState(
                    targetValue = if (dropState.isMenuOpen) 180f else 0f,
                    label = "Trailing Icon Rotation"
                )
                androidx.compose.material3.Icon(
                    Icons.Filled.KeyboardArrowDown,
                    modifier =
                        Modifier
                            .size(SplitButtonDefaults.TrailingIconSize)
                            .graphicsLayer {
                                this.rotationZ = rotation
                            },
                    contentDescription = ""
                )
            }
        }
    )
}


@Composable
fun <T> MenuButtonLayout(
    text: String,
    menuDialogState: MenuDialogState<T>,
    modifier: Modifier = Modifier
) {
    SplitButtonLayout(
        modifier = modifier,
        leadingButton = {
            SplitButtonDefaults.OutlinedLeadingButton(
                onClick = { /* Do Nothing */ },
            ) {
                Text(text)
            }
        },
        trailingButton = {
            SplitButtonDefaults.OutlinedTrailingButton(
                checked = menuDialogState.isShowing,
                onCheckedChange = {
                    menuDialogState.show()
                },
            ) {
                val rotation: Float by
                animateFloatAsState(
                    targetValue = if (menuDialogState.isShowing) 180f else 0f,
                    label = "Trailing Icon Rotation"
                )
                androidx.compose.material3.Icon(
                    Icons.Filled.KeyboardArrowDown,
                    modifier =
                        Modifier
                            .size(SplitButtonDefaults.TrailingIconSize)
                            .graphicsLayer {
                                this.rotationZ = rotation
                            },
                    contentDescription = ""
                )
            }
        }
    )
}

@Composable
fun ExpandIndicator(isExpand: Boolean, expand: (Boolean) -> Unit) {
    IconButton(
        onClick = {
            expand(!isExpand)
        },
    ) {
        val rotation: Float by
        animateFloatAsState(
            targetValue = if (isExpand) 180f else 0f,
            label = "Trailing Icon Rotation"
        )
        androidx.compose.material3.Icon(
            Icons.Filled.KeyboardArrowDown,
            modifier =
                Modifier
                    .size(SplitButtonDefaults.TrailingIconSize)
                    .graphicsLayer {
                        this.rotationZ = rotation
                    },
            contentDescription = ""
        )
    }
}

@Composable
fun ShortXFilledTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    liquidGlassTint: Color = Color.Unspecified,
    liquidGlassContentColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        content = content,
    )
}

@Composable
fun ShortXTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    liquidGlassContentColor: Color = Color.Unspecified,
    liquidGlassSurfaceColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        content = content,
    )
}