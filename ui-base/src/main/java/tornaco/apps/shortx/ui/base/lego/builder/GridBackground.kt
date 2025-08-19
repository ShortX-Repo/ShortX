package tornaco.apps.shortx.ui.base.lego.builder

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GridBackground(
    gridSize: Dp = 20.dp, // 每个网格的尺寸
    lineColor: Color = Color.LightGray // 网格线的颜色
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawGrid(gridSize, lineColor)
    }
}

fun DrawScope.drawGrid(gridSize: Dp, lineColor: Color) {
    val step = gridSize.toPx()

    // 画水平线
    for (y in 0..(size.height / step).toInt()) {
        val yOffset = y * step
        drawLine(
            color = lineColor,
            start = androidx.compose.ui.geometry.Offset(0f, yOffset),
            end = androidx.compose.ui.geometry.Offset(size.width, yOffset)
        )
    }

    // 画垂直线
    for (x in 0..(size.width / step).toInt()) {
        val xOffset = x * step
        drawLine(
            color = lineColor,
            start = androidx.compose.ui.geometry.Offset(xOffset, 0f),
            end = androidx.compose.ui.geometry.Offset(xOffset, size.height)
        )
    }
}

@Composable
fun GridPage(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        GridBackground() // 设置网格背景
        // 其他页面内容可以放在这
        content()
    }
}