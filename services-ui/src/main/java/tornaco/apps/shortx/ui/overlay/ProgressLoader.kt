package tornaco.apps.shortx.ui.overlay

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ProgressLoader() {
    val animation = rememberInfiniteTransition(label = "ProgressLoader")
    val rotation by animation.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            tween(8000, easing = LinearEasing),
            RepeatMode.Restart,
        ), label = "animateFloat"
    )

    Box(
        modifier = Modifier.size(300.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier
            .rotate(rotation)
            .drawBehind {
                drawArc(
                    color = Color.Black,
                    startAngle = 0f,
                    sweepAngle = 215f,
                    useCenter = false,

                    style = Stroke(
                        width = size.width * .195f,
                        cap = StrokeCap.Round
                    )
                )
            }
            .size(250.dp)
        )
        val x = 3
        for (i in 0..x) {
            Circle(i * (360f / x))
        }
    }
}


@Composable
fun Circle(offset: Float) {
    val animation = rememberInfiniteTransition(label = "Circle")
    val rotation by animation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(8000, easing = LinearEasing),
            RepeatMode.Restart,
        ), label = "animateFloat"
    )

    Box(
        modifier = Modifier
            .rotate(offset)
            .rotate(rotation)
            .width(300.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color.Black, CircleShape)
                .size(50.dp)
        )
    }
}