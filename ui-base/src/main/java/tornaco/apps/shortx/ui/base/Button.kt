@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

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