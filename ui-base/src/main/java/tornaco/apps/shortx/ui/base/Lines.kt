package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import tornaco.apps.shortx.ui.theme.NoteTextColor

@Composable
fun SplitLines(modifier: Modifier) {
    Canvas(modifier = modifier.fillMaxWidth(), onDraw = {
        drawLine(
            color = NoteTextColor,
            start = Offset.Zero,
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )
    })
}