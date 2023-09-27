@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package tornaco.apps.shortx.ui.base

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring.StiffnessMediumLow
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.theme.SuccessGreen
import tornaco.apps.shortx.ui.theme.WarningYellow

@Composable
fun ShortXCard(
    modifier: Modifier = Modifier,
    shape: Shape = ShortXCardRoundedCornerShape,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        backgroundColor = ColorDefaults.backgroundSurfaceColor(),
        shape = shape,
        elevation = 0.dp,
        onClick = onClick,
        content = content
    )
}

@Composable
fun TipCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        backgroundColor = SuccessGreen.copy(alpha = 0.16f),
        shape = ShortXCardRoundedCornerShape,
        elevation = 0.dp,
        content = content
    )
}

@Composable
fun TipCard(modifier: Modifier = Modifier, tip: String) {
    TipCard(modifier = modifier) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = tip,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
fun WarningCard(
    modifier: Modifier = Modifier,
    shape: Shape = ShortXCardRoundedCornerShape,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        backgroundColor = WarningYellow,
        shape = shape,
        elevation = 0.dp,
        onClick = onClick,
        content = content
    )
}

@Composable
fun ErrorCard(
    modifier: Modifier,
    title: String,
    iconName: String = Remix.System.error_warning_fill,
    warnings: List<String>, onClick: () -> Unit = {}
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row {
                val infiniteTransition = rememberInfiniteTransition()
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0.6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ), label = "scale"
                )

                RemixIcon(
                    modifier = Modifier
                        .offset(y = 2.dp)
                        .scale(scale),
                    remixName = iconName
                )
                MediumSpacer()
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 14.sp
                        )
                    )
                    MediumSpacer()
                    Text(
                        text = warnings.joinToString(System.lineSeparator()),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun InfoCard(title: String?, infos: List<String>, onClick: () -> Unit = {}) {
    androidx.compose.material3.Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row {
                RemixIcon(
                    modifier = Modifier.offset(y = 2.dp),
                    remixName = Remix.System.information_fill
                )
                MediumSpacer()
                Column {
                    title?.let {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 14.sp
                            )
                        )
                        MediumSpacer()
                    }
                    Text(
                        text = infos.joinToString(System.lineSeparator()),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ClickAnimateContainer(state: ClickAnimateState, content: @Composable (Modifier) -> Unit) {
    val widthPercent by animateFloatAsState(
        targetValue = if (!state.colorAnimStarted) 0f else 1f,
        animationSpec = spring(stiffness = StiffnessMediumLow),
        finishedListener = {
            state.colorAnimStarted = false
        }, label = "widthPercent"
    )
    val scalePercent by animateFloatAsState(
        targetValue = if (state.scaleAnimStarted) 0.96f else 1f,
        animationSpec = spring(),
        finishedListener = {
            state.scaleAnimStarted = false
        }, label = "scalePercent"
    )
    var itemSize by remember {
        mutableStateOf(Pair(0.dp, 0.dp))
    }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .scale(scalePercent),
    ) {
        Box {
            val modifier = Modifier.onSizeChanged {
                itemSize = with(density) {
                    it.width.toDp() to it.height.toDp()
                }
            }
            content(modifier)

            if (state.colorAnimStarted) Spacer(
                modifier = Modifier
                    .width((itemSize.first * widthPercent))
                    .height(itemSize.second)
                    .background(state.animateColor.copy(alpha = 0.07f))
            )
        }
    }
}

@Composable
fun rememberClickAnimateState(animateColor: Color): ClickAnimateState {
    return remember {
        ClickAnimateState(animateColor)
    }
}

class ClickAnimateState(val animateColor: Color) {
    var colorAnimStarted by mutableStateOf(false)
    var scaleAnimStarted by mutableStateOf(false)

    fun startAllAnim() {
        colorAnimStarted = true
        scaleAnimStarted = true
    }
}