package tornaco.apps.shortx.ui.base

import androidx.compose.animation.animateContentSize
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShortXCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.animateContentSize(),
        backgroundColor = ColorDefaults.backgroundSurfaceColor(),
        shape = ShortXCardRoundedCornerShape,
        elevation = 0.dp,
        content = content
    )
}