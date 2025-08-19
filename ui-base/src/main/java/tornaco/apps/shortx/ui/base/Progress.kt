package tornaco.apps.shortx.ui.base

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorPalette

@Composable
fun ColorfulCircularProgressIndicator(size: Dp = 48.dp, strokeWidth: Dp = 6.dp) {
    // 定义颜色列表
    val colors = ActionColorPalette

    // 用于追踪当前的颜色索引
    var currentColorIndex by remember { mutableIntStateOf(0) }

    // 动画颜色状态，用于平滑过渡
    val animatedColor by animateColorAsState(
        targetValue = colors[currentColorIndex],
        animationSpec = tween(durationMillis = 200),
        label = "animatedColor"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentColorIndex = (currentColorIndex + 1) % colors.size
        }
    }
    CircularProgressIndicator(
        modifier = Modifier.size(size),
        strokeWidth = strokeWidth,
        color = animatedColor
    )
}