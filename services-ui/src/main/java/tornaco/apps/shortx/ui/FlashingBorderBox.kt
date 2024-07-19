package tornaco.apps.shortx.ui


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FlashingBorderBox(content: @Composable () -> Unit) {
    val alpha = remember { Animatable(1f) }

    // Launch effect to start animation only once
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 500, // Duration of the flash
                easing = FastOutSlowInEasing
            )
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500, // Duration of the fade in
                easing = FastOutSlowInEasing
            )
        )
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 500, // Duration of the flash
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(
                width = 4.dp,
                color = Color.Red.copy(alpha = alpha.value),
                shape = MaterialTheme.shapes.medium
            )
    ) { content() }
}