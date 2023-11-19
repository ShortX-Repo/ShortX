package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp

@Composable
fun PaddingValues.copy(
    all: (current: Dp) -> Dp = { it },
): PaddingValues {
    return copy(
        start = all,
        top = all,
        end = all,
        bottom = all,
    )
}

@Composable
fun PaddingValues.copy(
    start: (current: Dp) -> Dp = { it },
    top: (current: Dp) -> Dp = { it },
    end: (current: Dp) -> Dp = { it },
    bottom: (current: Dp) -> Dp = { it }
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val currentStart = this.calculateStartPadding(layoutDirection)
    val currentTop = this.calculateTopPadding()
    val currentEnd = this.calculateEndPadding(layoutDirection)
    val currentBot = this.calculateBottomPadding()
    return PaddingValues(
        start = start(currentStart),
        top = top(currentTop),
        end = end(currentEnd),
        bottom = bottom(currentBot)
    )
}